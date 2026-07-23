import { ReactElement } from "react";
import { CiDiscount1 } from "react-icons/ci";

import { PromoItem } from "@/types";

export default function UnavailablePromoItemCard({
  promoItem,
}: {
  promoItem: PromoItem;
}): ReactElement {
  const { title, description } = promoItem;

  return (
    <div
      className={
        "flex justify-start items-center gap-4 lg:gap-5 rounded-xl border border-primary bg-[#F6F6F6] h-[76px] lg:h-[96px] p-4 lg:p-5"
      }
    >
      <CiDiscount1 className={"text-dark-grey-70"} size={32} />
      <div className={"flex flex-col gap-1 text-dark-grey-70"}>
        <p className={"text-lg"}>{title}</p>
        <p className={"text-base"}>{description}</p>
      </div>
    </div>
  );
}
