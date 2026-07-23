import { ReactElement, useState } from "react";

import { useGetPromosQuery } from "@/api/promos/promosApi.ts";
import { PromoItem } from "@/types";
import AvailablePromoItemCard from "@/components/ui/promo/available_promo_item_card.tsx";
import UnavailablePromoItemCard from "@/components/ui/promo/unavailable_promo_item_card.tsx";

export default function Promo({}: {}): ReactElement {
  const { data: promosProduct } = useGetPromosQuery({
    productId: 1,
  });

  return (
    <div className={"flex flex-col gap-6 lg:gap-8"}>
      <div className={"flex flex-col gap-1"}>
        <h4 className={"text-4xl lg:text-3xl"}>Discount Promo Code</h4>
        <p className={"text-base hidden md:block"}>
          Choose your best voucher discount
        </p>
      </div>
      <div className={"flex flex-col gap-3 lg:gap-4"}>
        <p className={"text-sm md:hidden"}>Choose your best voucher discount</p>
        <AvailablePromos
          availablePromos={promosProduct?.availablePromos ?? []}
        />
      </div>
      <div className={"flex flex-col gap-3 lg:gap-4"}>
        <h5 className={"text-sm lg:text-xl"}>
          Discount not available for this product
        </h5>
        <UnAvailablePromos
          unAvailablePromos={promosProduct?.unavailablePromos ?? []}
        />
      </div>
    </div>
  );
}

function AvailablePromos({
  availablePromos,
}: {
  availablePromos: PromoItem[];
}): ReactElement {
  const [isApply, setIsApply] = useState(false);

  return (
    <>
      {availablePromos.map((p: PromoItem) => (
        <AvailablePromoItemCard
          key={p.promoId}
          isApply={isApply}
          promoItem={p}
          setIsApply={setIsApply}
        />
      ))}
    </>
  );
}

function UnAvailablePromos({
  unAvailablePromos,
}: {
  unAvailablePromos: PromoItem[];
}): ReactElement {
  return (
    <div
      className={
        " max-h-[200px] overflow-scroll [scrollbar-width:none] [&::-webkit-scrollbar]:hidden  flex flex-col gap-3 lg:gap-4"
      }
    >
      {unAvailablePromos.map((p) => (
        <UnavailablePromoItemCard key={p.promoId} promoItem={p} />
      ))}
    </div>
  );
}
