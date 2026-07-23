import type { ReactElement } from "react";

import { Image } from "@heroui/image";

import PageHeading from "@/components/ui/page_heading.tsx";

export default function HomeCarousel(): ReactElement {
  return (
    <div
      className={
        "pt-[50px] flex flex-col my-[48px] md:my-[80px] gap-[32px] lg:gap-[80px]"
      }
    >
      <PageHeading
        className={"font-medium"}
        title={"Choose Your Coffee & Space"}
      />
      <Image
        alt={"hero_image"}
        className={"w-screen rounded-none object-center"}
        src={"/home/Sections/hero.png"}
      />
      <div
        className={
          "flex flex-col gap-[16px] p-[20px] md:flex-row items-center justify-center lg:px-[124px]"
        }
      >
        <p className={"text-base lg:text-xl md:w-1/2"}>
          IMAJI Coffee has been serving 20,000+ cups of coffee and providing a
          comfortable place for our customers to work since 2010.
        </p>
        <div className={"flex gap-[16px] md:justify-between md:w-1/2"}>
          <div className={"flex flex-col"}>
            <p className={"text-4xl lg:text-6xl"}>32+</p>
            <p className={"text-base"}>Variant Menu</p>
          </div>
          <div className={"flex flex-col"}>
            <p className={"text-4xl lg:text-6xl"}>8</p>
            <p className={"text-base"}>Comfy Space</p>
          </div>
          <div className={"flex flex-col"}>
            <p className={"text-4xl lg:text-6xl"}>500+</p>
            <p className={"text-base"}>Community Members</p>
          </div>
        </div>
        <div />
      </div>
    </div>
  );
}
