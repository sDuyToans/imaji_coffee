import { ReactElement } from "react";
import { Link } from "@heroui/link";
import { Image } from "@heroui/image";
import { IoClose } from "react-icons/io5";

import PageHeading from "@/components/ui/page_heading.tsx";

export default function DrawerHeading({
  heading,
  onClose,
}: {
  heading: string;
  onClose?: () => void;
}): ReactElement {
  return (
    <div className={"flex gap-8 justify-between items-center lg:items-start"}>
      <PageHeading
        className={"text-left !text5xl lg:!text-7xl"}
        title={heading}
      />
      <div className={"flex flex-col items-start gap-[24px]"}>
        {onClose && (
          <button
            className="lg:hidden p-2 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-full transition-colors"
            onClick={onClose}
          >
            <IoClose className="text-black dark:text-white" size={32} />
          </button>
        )}
        <h6 className={"hidden lg:block text-lg"}>Delivery Order</h6>
        <div className={"hidden lg:flex flex-row gap-6 w-[288px]"}>
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
    </div>
  );
}
