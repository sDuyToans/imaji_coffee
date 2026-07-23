import { ReactElement, useRef, useState } from "react";
import { BreadcrumbItem, Breadcrumbs } from "@heroui/breadcrumbs";
import { GoArrowLeft } from "react-icons/go";
import { useNavigate } from "react-router-dom";
import { FormProvider, useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import {
  CardNumberElement,
  useElements,
  useStripe,
} from "@stripe/react-stripe-js";
import toast from "react-hot-toast";

import DrawerHeading from "@/components/ui/drawer_heading.tsx";
import OrderItemsSummary from "@/components/ui/order/order_items_summary.tsx";
import PrimaryButton from "@/components/ui/button/primary_button.tsx";
import Address from "@/components/ui/checkout/address.tsx";
import Shipping from "@/components/ui/checkout/shipping.tsx";
import Payment from "@/components/ui/checkout/payment.tsx";
import { checkoutSchema } from "@/libs/yup/checkout_schema.ts";
import { ElementErrors, OrderItemRequest, OrderRequest } from "@/types";
import { useCreateOrderMutation } from "@/api/order/orderApi.ts";
import { useCart } from "@/context/cart.tsx";
import {
  useClearCartMutation,
  useClearPromoMutation,
  useClearShippingMutation,
  useGetCartQuery,
} from "@/api/cart/cartApi.ts";
import { useGetMeQuery } from "@/api/account/accountApi.ts";

enum Step {
  Address = 0,
  Shipping = 1,
  Payment = 2,
}

function extractCheckoutError(error: unknown): string {
  const data =
    typeof error === "object" && error !== null && "data" in error
      ? (error as { data?: unknown }).data
      : undefined;

  if (
    typeof data === "object" &&
    data !== null &&
    "message" in (data as { message?: unknown }) &&
    typeof (data as { message?: unknown }).message === "string"
  ) {
    return (data as { message: string }).message;
  }

  if (
    typeof data === "object" &&
    data !== null &&
    "errorMessage" in (data as { errorMessage?: unknown }) &&
    typeof (data as { errorMessage?: unknown }).errorMessage === "string"
  ) {
    return (data as { errorMessage: string }).errorMessage;
  }

  return "Checkout failed. Please review your items and try again.";
}

export default function Checkout({
  closeCheckout,
}: {
  closeCheckout: () => void;
}): ReactElement {
  const [step, setStep] = useState<Step>(Step.Address);
  const [isProcessing, setIsProcessing] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [cardName, setCardName] = useState("");
  const { data: cart } = useGetCartQuery();
  const cartItems = cart?.cartItems ?? [];

  const promoCode = cart?.promo?.code ?? null;
  const [createOrder] = useCreateOrderMutation();
  const [clearCart] = useClearCartMutation();
  const [clearPromo] = useClearPromoMutation();
  const { setIsOpenCart } = useCart();
  const [clearShip] = useClearShippingMutation();
  const { data: userInfo } = useGetMeQuery();

  const [elementErrors, setElementErrors] = useState<ElementErrors>({
    cardNumber: "",
    cardExpiry: "",
    cardCVC: "",
  });
  const checkoutAttemptKeyRef = useRef<string | null>(null);

  let email = userInfo?.email || "";

  const elements = useElements();
  const stripe = useStripe();
  const methods = useForm({
    mode: "onBlur", // validate only on Blur not on every key stroke
    shouldUnregister: false, // unmount fields not needed
    resolver: yupResolver(checkoutSchema),
    defaultValues: {
      userId: null,
      email: email,
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
      paymentMethod: "card",
      items: [],
    },
  });

  const { getValues, trigger } = methods;

  const handleBackStep = async (targetStep: Step) => {
    try {
      await clearShip().unwrap();
      setStep(targetStep);
    } catch {
      // Continue navigation even if clear shipping fails
    }
  };

  const handleNext = async () => {
    if (step === Step.Address) {
      const isValid = await trigger([
        "email",
        "shippingAddress.name",
        "shippingAddress.phoneNumber",
        "shippingAddress.street",
        "shippingAddress.city",
        "shippingAddress.country",
        "shippingAddress.postalCode",
        "shippingAddress.province",
        "shippingAddress.apartment",
      ]);

      if (!isValid) return;

      setStep(Step.Shipping);
    } else if (step === Step.Shipping) setStep(Step.Payment);
    else if (step === Step.Payment) {
      await handlePayment();
    }
  };

  const handlePayment = async () => {
    if (isProcessing) {
      return;
    }

    const paymentMethod = getValues("paymentMethod");

    if (paymentMethod === "card") {
      if (!stripe || !elements) {
        setErrorMessage("Stripe.js is not loaded yet.");

        return;
      }

      if (Object.values(elementErrors).some((error) => error)) {
        setErrorMessage("Please correct the highlighted errors.");

        return;
      }
    }

    setIsProcessing(true);

    try {
      const orderItems: OrderItemRequest[] = cartItems.map(
        (item): OrderItemRequest => ({
          productId: item.productId,
          quantity: item.quantity,
        }),
      );

      const payload: OrderRequest = {
        shippingAddress: {
          ...getValues("shippingAddress"),
          isDefault: !!getValues("shippingAddress.isDefault"),
        },
        couponCode: promoCode,
        idempotencyKey: checkoutAttemptKeyRef.current ?? crypto.randomUUID(),
        paymentMethod,
        shipMethodId: getValues("shipMethodId"),
        items: orderItems,
      };

      checkoutAttemptKeyRef.current = payload.idempotencyKey;

      if (paymentMethod === "card") {
        await handleCardPayment(payload);
      } else if (paymentMethod === "cod") {
        await handleCodPayment(payload);
      } else {
        throw new Error("Unsupported payment method");
      }
    } catch (error) {
      toast.error(extractCheckoutError(error));
    } finally {
      setIsProcessing(false);
    }
  };

  const handleCardPayment = async (payload: OrderRequest) => {
    if (!stripe || !elements) {
      throw new Error("Stripe.js is not loaded yet.");
    }

    const orderRes = await createOrder(payload).unwrap();
    const { orderId, clientSecret } = orderRes;

    if (!clientSecret) {
      throw new Error("Missing payment secret from server");
    }

    const cardElement = elements.getElement(CardNumberElement);

    if (!cardElement) {
      throw new Error("Card details are missing. Please try again");
    }

    const { error, paymentIntent } = await stripe.confirmCardPayment(
      clientSecret,
      {
        payment_method: {
          card: cardElement,
          billing_details: {
            name:
              cardName.trim() !== ""
                ? cardName
                : getValues("shippingAddress.name"),
            email: getValues("email"),
            phone: getValues("shippingAddress.phoneNumber"),
            address: {
              line1: getValues("shippingAddress.street"),
              city: getValues("shippingAddress.city"),
              state: getValues("shippingAddress.province"),
              postal_code: getValues("shippingAddress.postalCode"),
              country: getValues("shippingAddress.country"),
            },
          },
        },
      },
    );

    if (error) {
      toast.error(error.message || "Payment failed. Please try again.");
    } else if (paymentIntent && paymentIntent.status === "succeeded") {
      toast.success("Payment submitted. We will confirm it shortly.");
      clearCart();
      clearPromo();
      await clearShip().unwrap();
      setIsOpenCart(false);
      checkoutAttemptKeyRef.current = null;
      navigate(`/completed-checkout/${orderId}`);
    }
  };

  const handleCodPayment = async (payload: OrderRequest) => {
    const orderRes = await createOrder(payload).unwrap();
    const { orderId } = orderRes;

    toast.success("Order placed. Payment will be collected on delivery.", {
      duration: 5000,
    });
    clearCart();
    clearPromo();
    await clearShip().unwrap();
    setIsOpenCart(false);
    checkoutAttemptKeyRef.current = null;
    navigate(`/completed-checkout/${orderId}`);
  };

  const renderStepContent = () => {
    switch (step) {
      case Step.Address:
        return <Address />;
      case Step.Shipping:
        return <Shipping />;
      case Step.Payment:
        return (
          <Payment
            cardName={cardName}
            elementErrors={elementErrors}
            errorMessage={errorMessage}
            isProcessing={isProcessing}
            setCardName={setCardName}
            setElementErrors={setElementErrors}
          />
        );
    }
  };

  const navigate = useNavigate();

  return (
    <FormProvider {...methods}>
      <form onSubmit={methods.handleSubmit(handleNext)}>
        <div className={"flex flex-col lg:flex-row gap-8 lg:gap-[48px]"}>
          <div
            className={
              "lg:pt-[80px] lg:pb-[48px] flex flex-col gap-6 lg:gap-[48px] flex-4/5 pt-6  lg:px-[80px] px-5"
            }
          >
            <DrawerHeading heading={"Checkout"} onClose={closeCheckout} />
            <div>
              <Breadcrumbs>
                <BreadcrumbItem isCurrent={step === Step.Address}>
                  Checkout
                </BreadcrumbItem>
                <BreadcrumbItem isCurrent={step === Step.Shipping}>
                  Shipping
                </BreadcrumbItem>
                <BreadcrumbItem isCurrent={step === Step.Payment}>
                  Payment
                </BreadcrumbItem>
              </Breadcrumbs>
            </div>
            {cartItems.length > 0 ? (
              renderStepContent()
            ) : (
              <p>There is nothing to display, please order and come back</p>
            )}
            {step === Step.Address && (
              <PrimaryButton
                className={"hidden lg:flex"}
                type={"button"}
                onPress={closeCheckout}
              >
                <div className={"flex items-center gap-2"}>
                  <GoArrowLeft size={25} />
                  <p>Back</p>
                </div>
              </PrimaryButton>
            )}
            {step === Step.Shipping && (
              <PrimaryButton
                className={"hidden lg:flex"}
                type={"button"}
                onPress={() => handleBackStep(Step.Address)}
              >
                <div className={"flex items-center gap-2"}>
                  <GoArrowLeft size={25} />
                  <p>Back</p>
                </div>
              </PrimaryButton>
            )}
            {step === Step.Payment && (
              <PrimaryButton
                className={"hidden lg:flex"}
                type={"button"}
                onPress={() => handleBackStep(Step.Shipping)}
              >
                <div className={"flex items-center gap-2"}>
                  <GoArrowLeft size={25} />
                  <p>Back</p>
                </div>
              </PrimaryButton>
            )}
          </div>
          <OrderItemsSummary onClose={closeCheckout}>
            {step === Step.Address && (
              <PrimaryButton
                className={"w-full bg-primary text-white"}
                content={"Process to Shipping"}
                type={"button"}
                onPress={handleNext}
              />
            )}
            {step === Step.Shipping && (
              <PrimaryButton
                className={"w-full bg-primary text-white"}
                content={"Payment"}
                type={"button"}
                onPress={() => setStep(Step.Payment)}
              />
            )}
            {step === Step.Payment && (
              <PrimaryButton
                className={"w-full bg-primary text-white"}
                content={"Complete the Order"}
                type={"submit"}
              />
            )}
          </OrderItemsSummary>
        </div>
      </form>
    </FormProvider>
  );
}
