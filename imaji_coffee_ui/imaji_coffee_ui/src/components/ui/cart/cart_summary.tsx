import { ReactElement, ReactNode } from "react";
import { GoArrowLeft } from "react-icons/go";

import PrimaryButton from "@/components/ui/button/primary_button.tsx";
import { useGetCartQuery } from "@/api/cart/cartApi.ts";
import PromoCodeSection from "@/components/ui/promo/promo_code_section.tsx";

export default function CartSummary({
  onClose,
  children,
}: {
  onClose: () => void;
  summaryBtnContent: string;
  children?: ReactNode;
}): ReactElement {
  const { data: cart } = useGetCartQuery();

  const subtotal = cart?.subtotal || 0;
  const tax = cart?.tax || 0; // Or backend value if you send tax directly
  const total = cart?.total || 0;
  const discount = cart?.discount || 0;

  return (
    <div
      className={
        "flex-4/12 bg-[#FCF7EF] px-5 py-5 lg:px-[48px] lg:pt-[80px] flex flex-col gap-[48px] h-dvh"
      }
    >
      <p className={"text-4xl hidden lg:block dark:text-primary"}>
        Cart Summary
      </p>
      <div className={"flex flex-col gap-6"}>
        <div className={"flex justify-between items-center"}>
          <span className={"text-[#7F7F7F]"}>Subtotal</span>
          <span className={"text-base"}>${subtotal}</span>
        </div>
        <div className={"flex justify-between items-center"}>
          <span className={"text-[#7F7F7F]"}>Tax (10%)</span>
          <span className={"text-base"}>${tax}</span>
        </div>
        <PromoCodeSection />
      </div>
      <div className={"mt-auto pb-[80px] flex flex-col gap-[30px]"}>
        <div className={"flex justify-between items-center"}>
          <span className={"text-[#7F7F7F]"}>Total</span>
          <div>
            {!discount ? (
              <span className={"text-base"}>${total}</span>
            ) : (
              <div className={"flex flex-col gap-2"}>
                <span className={"text-2xl"}>${total}</span>
                <span className={"text-base line-through text-dark-grey-70"}>
                  ${subtotal + tax}
                </span>
              </div>
            )}
          </div>
        </div>
        {children}
      </div>
      <PrimaryButton className={"lg:hidden"} type={"button"} onPress={onClose}>
        <div className={"flex items-center gap-2"}>
          <GoArrowLeft size={25} />
          <p>Back</p>
        </div>
      </PrimaryButton>
    </div>
  );
}
