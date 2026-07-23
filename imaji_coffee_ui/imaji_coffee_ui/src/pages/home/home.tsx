import { type ReactElement } from "react";

import DefaultLayout from "@/layouts/default.tsx";
import HomeCarousel from "@/components/ui/home/home_carousel.tsx";
import HomeStory from "@/components/ui/home/home_story.tsx";
import HomeMenu from "@/components/ui/home/home_menu.tsx";
import HomeCoffeeApp from "@/components/ui/home/home_coffee_app.tsx";
import HomeSpace from "@/components/ui/home/home_space.tsx";
import HomeCommunityEvent from "@/components/ui/home/home_community_event.tsx";
import HomeNew from "@/components/ui/home/home_new.tsx";
import { useScrollAnimation } from "@/hooks/useScrollAnimation.ts";

export default function Home(): ReactElement {
  const story = useScrollAnimation();
  const menu = useScrollAnimation();
  const coffeeApp = useScrollAnimation();
  const space = useScrollAnimation();
  const community = useScrollAnimation();
  const news = useScrollAnimation();

  return (
    <DefaultLayout>
      <HomeCarousel />

      <div
        ref={story.ref}
        className={`transition-all duration-1000 ${
          story.isVisible
            ? "opacity-100 translate-y-0"
            : "opacity-0 translate-y-12"
        }`}
      >
        <HomeStory />
      </div>

      <div
        ref={menu.ref}
        className={`transition-all duration-1000 ${
          menu.isVisible
            ? "opacity-100 translate-y-0"
            : "opacity-0 translate-y-12"
        }`}
      >
        <HomeMenu />
      </div>

      <div
        ref={coffeeApp.ref}
        className={`transition-all duration-1000 ${
          coffeeApp.isVisible
            ? "opacity-100 translate-y-0"
            : "opacity-0 translate-y-12"
        }`}
      >
        <HomeCoffeeApp />
      </div>

      <div
        ref={space.ref}
        className={`transition-all duration-1000 ${
          space.isVisible
            ? "opacity-100 translate-y-0"
            : "opacity-0 translate-y-12"
        }`}
      >
        <HomeSpace />
      </div>

      <div
        ref={community.ref}
        className={`transition-all duration-1000 ${
          community.isVisible
            ? "opacity-100 translate-y-0"
            : "opacity-0 translate-y-12"
        }`}
      >
        <HomeCommunityEvent />
      </div>

      <div
        ref={news.ref}
        className={`transition-all duration-1000 ${
          news.isVisible
            ? "opacity-100 translate-y-0"
            : "opacity-0 translate-y-12"
        }`}
      >
        <HomeNew />
      </div>
    </DefaultLayout>
  );
}
