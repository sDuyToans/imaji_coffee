import { ReactElement, useState } from "react";

import CartLeft from "@/components/ui/cart/cart_left.tsx";
import CartSummary from "@/components/ui/cart/cart_summary.tsx";
import DrawerUI from "@/components/layouts/drawer.tsx";
import Checkout from "@/pages/checkout/checkout.tsx";
import PrimaryButton from "@/components/ui/button/primary_button.tsx";
import {
  useClearShippingMutation,
  useGetCartQuery,
} from "@/api/cart/cartApi.ts";

export default function Cart({
  onClose,
}: {
  onClose?: () => void;
}): ReactElement {
  const [isOpenCheckout, setIsOpenCheckout] = useState(false);
  const { data, refetch } = useGetCartQuery();
  const [clearShipping] = useClearShippingMutation();
  const cartItems = data?.cartItems ?? [];
  const handleBackToCart = async () => {
    try {
      await clearShipping().unwrap();
      await refetch();
      setIsOpenCheckout(false);
    } catch {
      // Silently handle error - shipping method will be cleared on next load
    }
  };

  const handleCloseCheckoutCompletely = async () => {
    try {
      await clearShipping().unwrap();
      await refetch();
      setIsOpenCheckout(false);
      // Close the entire cart drawer
      if (onClose) {
        onClose();
      }
    } catch {
      // Silently handle error - shipping method will be cleared on next load
    }
  };

  return (
    <div>
      <div className={"flex flex-col lg:flex-row gap-8 lg:gap-[48px]"}>
        <DrawerUI
          isOpen={isOpenCheckout}
          onClose={handleCloseCheckoutCompletely}
        >
          {cartItems && cartItems.length > 0 ? (
            <Checkout closeCheckout={handleBackToCart} />
          ) : (
            <p>There is nothing to display, please order and come back.</p>
          )}
        </DrawerUI>
        {onClose && <CartLeft onClose={onClose} />}
        {onClose && cartItems && cartItems.length > 0 && (
          <CartSummary
            summaryBtnContent={"Checkout"}
            onClose={handleBackToCart}
          >
            <PrimaryButton
              className={"w-full bg-primary text-white"}
              content={"Checkout"}
              type={"button"}
              onPress={() => setIsOpenCheckout(true)}
            />
          </CartSummary>
        )}
      </div>
    </div>
  );
}
