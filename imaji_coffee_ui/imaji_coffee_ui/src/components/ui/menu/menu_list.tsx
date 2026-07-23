import { Dispatch, ReactElement, SetStateAction } from "react";

import { useProducts } from "@/hooks/useProducts";
import { Option, ProductItem } from "@/types";
import ProductItemCard from "@/components/ui/product/product_item_card.tsx";
import { tabs } from "@/components/ui/menu/menu_config.ts";

type MenuListProps = {
  page: number;
  search: string;
  price: string;
  sort: string;
  activeCategory: string;
  onTabChange: (value: string) => void;
  setPage: Dispatch<SetStateAction<number>>;
};

export default function MenuList({
  page,
  search,
  price,
  sort,
  activeCategory,
  onTabChange,
  setPage,
}: MenuListProps): ReactElement {
  const { data, isFetching } = useProducts(
    activeCategory,
    page,
    search,
    price,
    sort,
  );

  let products = data?.items ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;

  const handleLoadMore = () => {
    if (page + 1 < totalPages) {
      setPage((prev) => prev + 1);
    }
  };

  return (
    <div>
      <div className={"flex flex-col gap-[48px]"}>
        <ul className={"uppercase flex justify-between w-full"}>
          {tabs.map(
            (tab: Option): ReactElement => (
              <li key={tab.key} className={"w-full flex-1"}>
                <button
                  className={`cursor-pointer px-4 py-2 w-full text-xl ${
                    activeCategory === tab.key
                      ? "border-b-1 border-neutral-900 dark:border-primary" +
                        " font-bold"
                      : ""
                  }`}
                  onClick={() => onTabChange(tab.key)}
                >
                  {tab.label}
                </button>
              </li>
            ),
          )}
        </ul>

        <div
          className={"grid grid-cols-2 lg:grid-cols-4 gap-6 lg:gap-y-[48px]"}
        >
          {products.map(
            (p: ProductItem): ReactElement => (
              <ProductItemCard key={p.productId} product={p} />
            ),
          )}
        </div>

        {/* Load More */}
        <div className="w-full flex flex-col items-center gap-4">
          {products.length > 0 && (
            <p className="text-sm text-gray-600 dark:text-gray-400">
              Showing {products.length} of {totalElements} products
            </p>
          )}
          {products.length < totalElements && (
            <button
              className="group relative px-8 py-3 border border-primary dark:border-white overflow-hidden transition-all duration-300 hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
              disabled={isFetching}
              type="button"
              onClick={handleLoadMore}
            >
              <span
                className={`absolute inset-0 bg-primary dark:bg-white transition-transform duration-300 ${
                  isFetching
                    ? "translate-y-0"
                    : "translate-y-full group-hover:translate-y-0"
                }`}
              />
              <span className="relative flex items-center gap-2 text-primary dark:text-white group-hover:text-white dark:group-hover:text-gray-900 transition-colors duration-300">
                {isFetching ? (
                  <>
                    <svg
                      className="animate-spin h-4 w-4"
                      fill="none"
                      viewBox="0 0 24 24"
                    >
                      <circle
                        className="opacity-25"
                        cx="12"
                        cy="12"
                        r="10"
                        stroke="currentColor"
                        strokeWidth="4"
                      />
                      <path
                        className="opacity-75"
                        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                        fill="currentColor"
                      />
                    </svg>
                    Loading...
                  </>
                ) : (
                  <>
                    Load More
                    <svg
                      className="w-4 h-4 transition-transform group-hover:translate-y-0.5"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        d="M19 9l-7 7-7-7"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                      />
                    </svg>
                  </>
                )}
              </span>
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
