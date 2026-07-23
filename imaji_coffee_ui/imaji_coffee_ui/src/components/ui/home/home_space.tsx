import { ReactElement } from "react";
import { Spinner } from "@heroui/spinner";

import PageHeading from "@/components/ui/page_heading.tsx";
import CustomCarousel from "@/libs/slider/custom_carousel.tsx";
import { useGetSpacesQuery } from "@/api/spaces/spacesApi.ts";

export default function HomeSpace(): ReactElement {
  return (
    <div
      className={
        "flex flex-col gap-[48px] lg:gap-[56px] my-[48px] lg:my-[80px] px-5 lg:px-[124px]"
      }
    >
      <SpaceTitle />
      <SpaceContent />
    </div>
  );
}

function SpaceTitle(): ReactElement {
  return (
    <div>
      <PageHeading className={"text-left"} title={"8 Comfy Workspace"} />
      <p className={"text-base lg:text-xl lg:w-1/2"}>
        We provide many attractive and unique workspaces so you will have no
        trouble finding the workspace you want.
      </p>
    </div>
  );
}

function SpaceContent(): ReactElement {
  const { data: spaces, isLoading } = useGetSpacesQuery();

  return (
    <div>
      {isLoading && <Spinner color={"primary"} />}
      {spaces && (
        <CustomCarousel isLoop={true} isShowControl={true} slides={spaces} />
      )}
    </div>
  );
}
