import { Dispatch, ReactElement, SetStateAction } from "react";
import { CiDiscount1 } from "react-icons/ci";

import PrimaryButton from "@/components/ui/button/primary_button.tsx";
import { PromoItem } from "@/types";
import { useUpdatePromoMutation } from "@/api/cart/cartApi.ts";

export default function AvailablePromoItemCard({
  promoItem,
  isApply,
  setIsApply,
}: {
  promoItem: PromoItem;
  isApply: boolean;
  setIsApply: Dispatch<SetStateAction<boolean>>;
}): ReactElement {
  const { title, description } = promoItem;
  const [updatePromo] = useUpdatePromoMutation();

  const handleApplyPromo = () => {
    setIsApply(true);
    updatePromo(promoItem.promoId);
  };

  return (
    <div
      className={
        "flex justify-center items-center gap-2 md:gap-8 rounded-3xl border border-primary h-[96px] lg:h-[106px] p-4 lg:p-5"
      }
    >
      <CiDiscount1 className={"text-primary flex-1/4"} size={32} />
      <div className={"flex flex-col gap-1 w-full"}>
        <p className={"text-lg"}>{title}</p>
        <p className={"text-dark-grey-70 text-base"}>{description}</p>
      </div>
      <PrimaryButton
        className={"bg-primary w-[73px] text-white flex-1/4"}
        content={"Apply"}
        disabled={isApply}
        type={"button"}
        onPress={handleApplyPromo}
      />
    </div>
  );
}
