import { type ReactElement } from "react";

import DefaultLayout from "@/layouts/default.tsx";
import HomeNew from "@/components/ui/home/home_new.tsx";
import NewList from "@/components/ui/news/new_list.tsx";
import { useScrollAnimation } from "@/hooks/useScrollAnimation.ts";

/**
 * @author duytoan
 * @since 05/2026
 */
export default function News(): ReactElement {
  const heroSection = useScrollAnimation();
  const listSection = useScrollAnimation();

  return (
    <DefaultLayout>
      <div className="py-[48px] lg:py-[80px] flex flex-col gap-[48px] lg:gap-[64px]">
        {/* Hero Section with animation */}
        <div
          ref={heroSection.ref}
          className={`transition-all duration-1000 ${
            heroSection.isVisible
              ? "opacity-100 translate-y-0"
              : "opacity-0 translate-y-12"
          }`}
        >
          <HomeNew />
        </div>

        {/* News List Section with staggered animation */}
        <div
          ref={listSection.ref}
          className={`transition-all duration-1000 delay-200 ${
            listSection.isVisible
              ? "opacity-100 translate-y-0"
              : "opacity-0 translate-y-12"
          }`}
        >
          <NewList />
        </div>
      </div>
    </DefaultLayout>
  );
}
