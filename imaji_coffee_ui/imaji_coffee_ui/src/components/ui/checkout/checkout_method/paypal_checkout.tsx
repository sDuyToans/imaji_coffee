import React, { useRef, useState } from "react";
import {
  DISPATCH_ACTION,
  PayPalButtons,
  usePayPalScriptReducer,
} from "@paypal/react-paypal-js";
import { Spinner } from "@heroui/spinner";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

import PayPalProvider from "@/providers/paypal_provider.tsx";
import {
  useClearCartMutation,
  useClearPromoMutation,
  useClearShippingMutation,
  useGetCartQuery,
} from "@/api/cart/cartApi.ts";
import { checkoutSchema } from "@/libs/yup/checkout_schema.ts";
import { OrderItemRequest, OrderRequest } from "@/types";
import { useCreateOrderForPayPalMutation } from "@/api/order/orderApi.ts";
import { useGetMeQuery } from "@/api/account/accountApi.ts";

export default function PaypalCheckout(): React.ReactElement {
  return (
    <div className={"w-full"}>
      <PayPalProvider>
        <PaymentContainer />
      </PayPalProvider>
    </div>
  );
}

function PaymentContainer(): React.ReactElement {
  const [{ options, isPending }, dispatch] = usePayPalScriptReducer();
  const [currency, setCurrency] = useState(options.currency);
  const [createdInternalOrderId, setCreatedInternalOrderId] = useState<
    number | null
  >(null);
  const checkoutAttemptKeyRef = useRef<string | null>(null);
  const { data: cart } = useGetCartQuery();
  const cartItems = cart?.cartItems ?? [];
  const [createOrderForPayPal] = useCreateOrderForPayPalMutation();
  const [clearCart] = useClearCartMutation();
  const [clearPromo] = useClearPromoMutation();
  const [clearShip] = useClearShippingMutation();
  const { data: userInfo } = useGetMeQuery();
  const navigate = useNavigate();
  let emailFromCookie = userInfo?.email ?? "";

  const methods = useForm({
    mode: "onBlur", // validate only on Blur not on every keystroke
    shouldUnregister: false, // unmount fields not needed
    resolver: yupResolver(checkoutSchema),
    defaultValues: {
      userId: null,
      email: emailFromCookie,
      shipMethodId: 1,
      shippingAddress: {
        name: "",
        country: "",
        province: "",
        city: "",
        postalCode: "",
        street: "",
        apartment: "",
        phoneNumber: "",
      },
      paymentMethod: "paypal",
      items: [],
    },
  });

  const { getValues } = methods;

  const promoCode = cart?.promo?.code ?? null;

  // currency selector 'USD' and 'EUR'
  const onCurrencyChange = ({
    target: { value },
  }: {
    target: { value: string };
  }) => {
    setCurrency(value);
    dispatch({
      type: DISPATCH_ACTION.RESET_OPTIONS,
      value: {
        ...options,
        currency: value,
      },
    });
  };
  // onCreateOrderPayPal
  const onCreateOrder = async (data: any, actions: any) => {
    const payload: OrderRequest = {
      shippingAddress: {
        ...getValues("shippingAddress"),
        isDefault: !!getValues("shippingAddress.isDefault"),
      },
      couponCode: promoCode,
      idempotencyKey: checkoutAttemptKeyRef.current ?? crypto.randomUUID(),
      paymentMethod: data.paymentSource,
      shipMethodId: getValues("shipMethodId"),
      items: cartItems.map((item: OrderItemRequest) => ({
        productId: item.productId,
        quantity: item.quantity,
      })),
    };

    checkoutAttemptKeyRef.current = payload.idempotencyKey;

    const createdOrder = await createOrderForPayPal(payload).unwrap();

    setCreatedInternalOrderId(createdOrder.orderId);

    return actions.order.create({
      purchase_units: [
        {
          amount: {
            currency_code: createdOrder.currency,
            value: createdOrder.totalAmount.toFixed(2),
          },
          custom_id: String(createdOrder.orderId),
          invoice_id: String(createdOrder.orderId),
        },
      ],
    });
  };

  // on Approve Order PayPal
  const onApproveOrder = async (data: any) => {
    // clear cart, promo, and ship
    await clearCart();
    await clearPromo();
    await clearShip().unwrap();

    toast.success("Payment submitted. We will confirm it shortly.");
    const internalOrderId = createdInternalOrderId ?? Number(data.orderID);

    if (Number.isFinite(internalOrderId)) {
      checkoutAttemptKeyRef.current = null;
      navigate(`/completed-checkout/${internalOrderId}`);
    } else {
      navigate("/account");
    }
  };

  if (isPending) {
    return <Spinner color={"primary"} />;
  }

  return (
    <div className={"w-full"}>
      <div>
        <label htmlFor={"cur"}>Currency Selection:</label> <br />
        <select id="cur" value={currency} onChange={onCurrencyChange}>
          <option value={"USD"}>USD</option>
          <option value={"EUR"}>EUR</option>
        </select>
      </div>
      <PayPalButtons
        className={"w-1/4"}
        createOrder={(data, actions) => onCreateOrder(data, actions)}
        style={{ layout: "vertical" }}
        onApprove={(data) => onApproveOrder(data)}
      />
    </div>
  );
}
