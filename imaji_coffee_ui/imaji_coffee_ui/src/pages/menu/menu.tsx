import { ReactElement, useState, useEffect } from "react";

import DefaultLayout from "@/layouts/default.tsx";
import { MenuHeading } from "@/components/ui/menu/menu_heading.tsx";
import MenuList from "@/components/ui/menu/menu_list.tsx";
import FilterMenu from "@/components/ui/menu/filter_menu.tsx";
import { tabs } from "@/components/ui/menu/menu_config.ts";

export default function Menu(): ReactElement {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    setIsVisible(true);
  }, []);

  return (
    <DefaultLayout>
      <div className="py-[48px] lg:py-[80px] flex flex-col gap-[48px]">
        <div
          className={`transition-all duration-1000 ${
            isVisible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-12"
          }`}
        >
          <MenuHeading />
        </div>
        <MenuContent isVisible={isVisible} />
      </div>
    </DefaultLayout>
  );
}

function MenuContent({ isVisible }: { isVisible: boolean }): ReactElement {
  const [search, setSearch] = useState("");
  const [price, setPrice] = useState("all");
  const [sort, setSort] = useState("all");
  const [page, setPage] = useState(0);

  const [activeCategory, setActiveCategory] = useState(tabs[0].key);

  const handleTabChange = (category: string) => {
    setActiveCategory(category);
    setSearch(""); // reset search when switching category
    setPage(0);
  };

  return (
    <div className="px-5 lg:px-[124px] flex flex-col gap-[48px]">
      <div
        className={`transition-all duration-1000 delay-200 ${
          isVisible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-12"
        }`}
      >
        <FilterMenu
          price={price}
          search={search}
          setPrice={(v) => {
            setPrice(v);
            setPage(0);
          }}
          setSearch={(v) => {
            setSearch(v);
            setPage(0);
          }}
          setSort={(v) => {
            setSort(v);
            setPage(0);
          }}
          sort={sort}
        />
      </div>
      <div
        className={`transition-all duration-1000 delay-300 ${
          isVisible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-12"
        }`}
      >
        <MenuList
          activeCategory={activeCategory}
          page={page}
          price={price}
          search={search}
          setPage={setPage}
          sort={sort}
          onTabChange={handleTabChange}
        />
      </div>
    </div>
  );
}
