import { ReactElement } from "react";

import PageHeading from "@/components/ui/page_heading.tsx";
import { ProductItem } from "@/types";
import ProductContainer from "@/components/ui/product/product_container.tsx";
import PrimaryLink from "@/components/ui/button/primary_link.tsx";
import { useGetProductsBySizeQuery } from "@/api/products/productsApi.ts";

export default function HomeMenu(): ReactElement {
  const {
    data: products,
    isLoading,
    isFetching,
  } = useGetProductsBySizeQuery({ size: 4 });
  const isInitialLoading = isLoading && !products;
  const menuProducts = products ?? [];

  return (
    <div
      className={
        "px-[20px] py-[48px] md:my-[80px] flex flex-col gap-[48px] md:gap-[56px] lg:px-[124px]"
      }
    >
      <MenuTitle />
      {isInitialLoading ? (
        <MenuLoadingState />
      ) : menuProducts.length > 0 ? (
        <div className="space-y-4">
          {isFetching && (
            <p className="text-sm text-gray-500 dark:text-gray-400">
              Updating menu...
            </p>
          )}
          <MenuContent products={menuProducts} />
        </div>
      ) : (
        <MenuEmptyState />
      )}
    </div>
  );
}

function MenuTitle(): ReactElement {
  return (
    <div
      className={
        "flex flex-col md:flex-row items-left md:items-center gap-[32px] max-w-[1192px]"
      }
    >
      <PageHeading className={"text-left"} title={"Find Your Favorite Menu"} />
      <PrimaryLink content={"Explore Other Menu"} to={"/menu"} />
    </div>
  );
}

function MenuContent({ products }: { products: ProductItem[] }): ReactElement {
  return (
    <div>
      <ProductContainer products={products} />
    </div>
  );
}

function MenuLoadingState(): ReactElement {
  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-x-[24px] gap-y-[32px] md:gap-[32px] lg:gap-[48px]">
      {Array.from({ length: 4 }).map((_, index) => (
        <div
          key={index}
          className="animate-pulse border border-gray-200 dark:border-gray-800"
        >
          <div className="w-full h-[160px] lg:h-[300px] bg-gray-200 dark:bg-gray-800" />
          <div className="p-4 space-y-3">
            <div className="h-4 w-3/4 bg-gray-200 dark:bg-gray-800" />
            <div className="h-3 w-1/2 bg-gray-200 dark:bg-gray-800" />
            <div className="h-4 w-1/3 bg-gray-200 dark:bg-gray-800" />
          </div>
        </div>
      ))}
    </div>
  );
}

function MenuEmptyState(): ReactElement {
  return (
    <div className="rounded-2xl border border-dashed border-gray-300 bg-gray-50 px-6 py-14 text-center dark:border-gray-700 dark:bg-gray-950">
      <h3 className="text-xl font-medium text-gray-900 dark:text-white">
        No menu items yet
      </h3>
      <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">
        We couldn’t load the featured products right now. Please try again in a
        moment.
      </p>
    </div>
  );
}
