import { ReactElement } from "react";
import { GoArrowLeft } from "react-icons/go";
import { Link } from "@heroui/link";

import PrimaryButton from "@/components/ui/button/primary_button.tsx";
// import { CartItem, selectCartItem } from "@/features/cart/cartSlice.ts";
import CartItems from "@/components/ui/cart/cart_items.tsx";
import DrawerHeading from "@/components/ui/drawer_heading.tsx";
import { useCart } from "@/context/cart.tsx";
import { useGetCartQuery } from "@/api/cart/cartApi.ts";

export default function CartLeft({
  onClose,
}: {
  onClose: () => void;
}): ReactElement {
  const { data } = useGetCartQuery();
  const { setIsOpenCart } = useCart();
  const items = data?.cartItems ?? [];

  return (
    <div
      className={
        "flex flex-col gap-6 lg:gap-[48px] flex-4/5 pt-6 lg:pt-[80px] lg:pb-[48px] lg:px-[80px] px-5"
      }
    >
      {items && items.length > 0 ? (
        <>
          <DrawerHeading
            heading={"My Cart"}
            onClose={() => setIsOpenCart(false)}
          />
          <CartItems items={items} />
          <PrimaryButton
            className={"hidden lg:flex"}
            type={"button"}
            onPress={onClose}
          >
            <div className={"flex items-center gap-2"}>
              <GoArrowLeft size={25} />
              <p>Back</p>
            </div>
          </PrimaryButton>
        </>
      ) : (
        <div>
          <h5 className={"text-2xl lg:text-5xl"}>
            There is nothing in the cart, please order and come back
          </h5>
          <PrimaryButton type={"button"}>
            <Link href={"/menu"} onPress={() => setIsOpenCart(false)}>
              Go to Menu
            </Link>
          </PrimaryButton>
        </div>
      )}
    </div>
  );
}
