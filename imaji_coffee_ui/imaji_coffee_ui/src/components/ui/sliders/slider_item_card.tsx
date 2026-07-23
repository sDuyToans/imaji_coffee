import { ReactElement } from "react";
import { Card, CardBody } from "@heroui/card";

import { EventItem, SliderItem } from "@/types";

export default function SliderItemCard({
  item,
  descPos = "top",
  styles = "",
}: {
  item: SliderItem | EventItem;
  styles?: string;
  descPos?: "top" | "bottom";
}): ReactElement {
  const { name, image } = item;
  let type: String = "";
  let start_date: String = "";

  if ("type" in item) {
    type = item.type;
  } else {
    start_date = item.start_date;
  }
  let pos: string = descPos === "top" ? "top-0" : "bottom-0";

  return (
    <Card className={`rounded-none p-0 ${styles}`}>
      <CardBody className={"p-0 relative"}>
        <img alt={name} className={"w-full h-full object-cover"} src={image} />
        <div
          className={`flex flex-col p-2 justify-center items-center min-w-[133px] min-h-[68px] lg:min-w-[165px] lg:min-h-[84px] bg-white absolute left-0 z-999 ${pos}`}
        >
          <p className={"capitalize text-base lg:text-xl dark:text-black"}>
            {name}
          </p>
          <p className={"text-xs lg:text-sm text-[#A27B5C]"}>{start_date}</p>
          <p className={"text-xs lg:text-sm text-[#A27B5C]"}>{type}</p>
        </div>
      </CardBody>
    </Card>
  );
}
