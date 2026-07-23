import { useEffect, useState, type ReactElement } from "react";
import { Spinner } from "@heroui/spinner";

import { NewItem } from "@/types";
import NewItemCard from "@/components/ui/news/new_item_card.tsx";
import { useGetNewsQuery } from "@/api/news/newsApi.ts";

/**
 * @author duytoan
 * @since 05/2026
 */
export default function NewList(): ReactElement {
  const { data: news, isLoading } = useGetNewsQuery();
  const [visibleItems, setVisibleItems] = useState<number[]>([]);

  // Stagger animation for news items
  useEffect(() => {
    if (!news?.length) return;

    const timers = news.map((_, index) =>
      setTimeout(() => {
        setVisibleItems((prev) => [...prev, index]);
      }, index * 100),
    );

    return () => {
      timers.forEach((timer) => clearTimeout(timer));
    };
  }, [news]);

  const handleLoadMore: () => void = (): void => {};

  return (
    <div className="px-5 py-[48px] lg:py-[80px] flex flex-col gap-8 lg:gap-12 lg:px-[124px]">
      {/* Loading State */}
      {isLoading && (
        <div className="flex justify-center items-center py-20">
          <Spinner color="primary" size="lg" />
        </div>
      )}

      {/* News Items with staggered animation */}
      {!isLoading && news && news.length > 0 ? (
        <>
          <div className="mb-4">
            <h2 className="text-2xl lg:text-3xl font-medium">All Articles</h2>
            <div className="mt-4 w-20 h-1 bg-primary dark:bg-white" />
          </div>

          <div className="flex flex-col divide-y divide-gray-200 dark:divide-gray-800">
            {news.map(
              (n: NewItem, index: number): ReactElement => (
                <div
                  key={n.newId}
                  className={`transition-all duration-700 ${
                    visibleItems.includes(index)
                      ? "opacity-100 translate-y-0"
                      : "opacity-0 translate-y-8"
                  }`}
                >
                  <NewItemCard newItem={n} />
                </div>
              ),
            )}
          </div>
        </>
      ) : null}

      {/* No News State */}
      {!isLoading && (!news || news.length === 0) && (
        <div className="flex flex-col items-center justify-center py-20">
          <p className="text-gray-500 dark:text-gray-400">
            No news available at the moment
          </p>
        </div>
      )}

      {/* Load More Button */}
      {!isLoading && news && news.length > 0 && (
        <div className="w-full flex flex-col items-center gap-4 pt-4">
          <p className="text-sm text-gray-600 dark:text-gray-400">
            Showing {news.length} articles
          </p>
          <button
            className="group relative px-8 py-3 border border-primary dark:border-white overflow-hidden transition-all duration-300 hover:scale-105"
            type="button"
            onClick={handleLoadMore}
          >
            <span className="absolute inset-0 bg-primary dark:bg-white transition-transform duration-300 translate-y-full group-hover:translate-y-0" />
            <span className="relative flex items-center gap-2 text-primary dark:text-white group-hover:text-white dark:group-hover:text-gray-900 transition-colors duration-300">
              Load More Articles
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
            </span>
          </button>
        </div>
      )}
    </div>
  );
}
