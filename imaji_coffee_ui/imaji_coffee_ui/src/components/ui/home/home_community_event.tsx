import { ReactElement } from "react";

import PageHeading from "@/components/ui/page_heading.tsx";
import PrimaryLink from "@/components/ui/button/primary_link.tsx";
import { SliderItem } from "@/types";
import CustomCarousel from "@/libs/slider/custom_carousel.tsx";
import Podcast from "@/components/ui/home/podcast.tsx";

export default function HomeCommunityEvent(): ReactElement {
  return (
    <div className={"flex flex-col gap-[56px] py-[80px]"}>
      <Title />
      <Content />
      <Podcast />
    </div>
  );
}

function Title(): ReactElement {
  return (
    <div className={"px-5 lg:px-[124px]"}>
      <PageHeading
        className={"text-left"}
        title={"We Grow Together With Out Customer"}
      />
    </div>
  );
}

function Content(): ReactElement {
  const eventItems: SliderItem[] = [
    {
      id: 1,
      name: "latter art workshop",
      type: "20 Feb 2023",
      image: "/home/event/card/Sections/Image1.png",
    },
    {
      id: 2,
      name: "EXHIBITION COFFEE HARDWARE",
      type: "20 Mar 2023",
      image: "/home/event/card/Sections/Image2.png",
    },
    {
      id: 3,
      name: "Factory visit",
      type: "20 Apr 2023",
      image: "/home/event/card/Sections/Image3.png",
    },
  ];

  return (
    <div
      className={
        "px-5 lg:pl-[124px] flex flex-col gap-6 lg:flex-row lg:gap-[48px] items-center"
      }
    >
      <div className={"flex flex-col gap-6 lg:gap-[80px] lg:flex-3/5"}>
        <p>
          We believe that we are big not because of us but because of them. they
          are the ones who motivate us to continue to innovate to provide a
          quality coffee taste and comfortable space that is getting better
          every day.
        </p>
        <PrimaryLink content={"Explore Other Event"} to={"/community"} />
      </div>
      <CustomCarousel
        isLoop={true}
        isShowControl={false}
        pos={"bottom"}
        slides={eventItems}
      />
    </div>
  );
}
