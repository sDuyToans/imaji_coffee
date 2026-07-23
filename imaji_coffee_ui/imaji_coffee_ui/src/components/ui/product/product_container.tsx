import { ReactElement } from "react";

import { ProductItem } from "@/types";
import ProductItemCard from "@/components/ui/product/product_item_card.tsx";

export default function ProductContainer({
  products,
}: {
  products: ProductItem[];
}): ReactElement {
  return (
    <div
      className={
        "grid grid-cols-2 md:grid-cols-4 gap-x-[24px] gap-y-[32px] md:gap-[32px] lg:gap-[48px]"
      }
    >
      {products.map(
        (p: ProductItem): ReactElement => (
          <ProductItemCard key={p.productId} product={p} />
        ),
      )}
    </div>
  );
}
