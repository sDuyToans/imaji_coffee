import type { ReactElement } from "react";

import DefaultLayout from "@/layouts/default.tsx";
import UpComingEvent from "@/components/ui/event/up_coming_event.tsx";
import ClosedEvent from "@/components/ui/event/closed_event.tsx";
import { useScrollAnimation } from "@/hooks/useScrollAnimation.ts";

export default function Event(): ReactElement {
  const upcomingSection = useScrollAnimation();
  const closedSection = useScrollAnimation();

  return (
    <DefaultLayout>
      <div className="py-[48px] lg:py-[80px] flex flex-col gap-[80px]">
        <div
          ref={upcomingSection.ref}
          className={`transition-all duration-1000 ${
            upcomingSection.isVisible
              ? "opacity-100 translate-y-0"
              : "opacity-0 translate-y-12"
          }`}
        >
          <UpComingEvent />
        </div>

        <div
          ref={closedSection.ref}
          className={`transition-all duration-1000 ${
            closedSection.isVisible
              ? "opacity-100 translate-y-0"
              : "opacity-0 translate-y-12"
          }`}
        >
          <ClosedEvent />
        </div>
      </div>
    </DefaultLayout>
  );
}
