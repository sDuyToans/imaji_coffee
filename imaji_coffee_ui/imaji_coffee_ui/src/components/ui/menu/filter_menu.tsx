import { ReactElement } from "react";

import FilterInput from "@/components/ui/filter/FilterInput.tsx";
import FilterSelect from "@/components/ui/filter/FilterSelect.tsx";
import {
  bestSellingSelects,
  priceSelects,
} from "@/components/ui/menu/menu_config.ts";

type FilterMenuProps = {
  search: string;
  price: string;
  sort: string;
  setSearch: (value: string) => void;
  setPrice: (value: string) => void;
  setSort: (value: string) => void;
};

export default function FilterMenu({
  setSearch,
  search,
  price,
  setPrice,
  sort,
  setSort,
}: FilterMenuProps): ReactElement {
  const hasActiveFilters = search !== "" || price !== "all" || sort !== "all";

  const handleClearAll = () => {
    setSearch("");
    setPrice("all");
    setSort("all");
  };

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-medium">Filter & Search</h3>
        {hasActiveFilters && (
          <button
            className="text-sm text-primary dark:text-white hover:underline transition-all"
            onClick={handleClearAll}
          >
            Clear All Filters
          </button>
        )}
      </div>

      <div className="flex flex-col md:flex-row gap-6 p-6 bg-gray-50 dark:bg-gray-900 border border-gray-200 dark:border-gray-800">
        <FilterInput label={"Search"} value={search} onChange={setSearch} />

        <div className="flex-1 flex flex-col md:flex-row gap-6">
          <FilterSelect
            label="Price"
            options={priceSelects}
            value={price}
            onChange={setPrice}
          />
          <FilterSelect
            label="Sort By"
            options={bestSellingSelects}
            value={sort}
            onChange={setSort}
          />
        </div>
      </div>
    </div>
  );
}
