import { ReactElement } from "react";
import { Link } from "@heroui/link";
import { Image } from "@heroui/image";

import PageHeading from "@/components/ui/page_heading.tsx";

export function MenuHeading(): ReactElement {
  return (
    <div className={"px-5 lg:px-[124px] flex flex-col lg:flex-row gap-[48px]"}>
      <div className={"flex-1/2"}>
        <PageHeading className={"text-left"} title={"Our Menu"} />
        <p className={"text-base lg:text-xl"}>
          IMAJI Coffee provides a variety of high quality coffee and drinks and
          flavors that are suitable for you to support and cheer up your day. We
          also provide coffee to accompany you at home along with the equipment.
        </p>
      </div>
      <div className={"flex flex-col lg:items-end gap-4 lg:gap-5 flex-1"}>
        <p className={"text-base lg:text-xl"}>Delivery Order</p>
        <Link href={"#"}>
          <Image
            alt={"app_store"}
            className={"rounded-none"}
            src={"/footer/logo_app_store.png"}
          />
        </Link>
        <Link href={"#"}>
          <Image
            alt={"app_store"}
            className={"rounded-none"}
            src={"/footer/google_play.png"}
          />
        </Link>
      </div>
    </div>
  );
}
