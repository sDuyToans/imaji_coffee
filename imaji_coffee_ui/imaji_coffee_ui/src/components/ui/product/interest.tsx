import { ReactElement } from "react";
import { Spinner } from "@heroui/spinner";

import { ProductCategory, ProductItem } from "@/types";
import { useGetRelatedProductsQuery } from "@/api/products/productsApi.ts";
import PageHeading from "@/components/ui/page_heading.tsx";
import ProductItemCard from "@/components/ui/product/product_item_card.tsx";

export default function MayInterest({
  category,
  excludedId,
}: {
  category: ProductCategory;
  excludedId: number;
}): ReactElement {
  const { data: products, isLoading } = useGetRelatedProductsQuery({
    category,
    size: 4,
    excludedId: excludedId,
  });

  if (isLoading)
    return (
      <div className={"bg-[#FCF7EF] py-[48px] lg:py-[80px]"}>
        <Spinner color={"primary"} />
      </div>
    );

  return (
    <div className={"bg-[#FCF7EF] py-[48px] lg:py-[80px]"}>
      <div
        className={"px-5 lg:px-[124px] flex flex-col gap-[48px] lg:gap-[56px]"}
      >
        <PageHeading
          className={"text-left dark:text-primary"}
          title={"You May Also Like"}
        />
        <div className={"grid grid-cols-2 lg:grid-cols-4 gap-6"}>
          {products &&
            products.map(
              (p: ProductItem): ReactElement => (
                <ProductItemCard key={p.productId} product={p} />
              ),
            )}
        </div>
      </div>
    </div>
  );
}
