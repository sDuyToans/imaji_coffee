import type { ReactElement, ReactNode } from "react";

import { Link } from "@heroui/link";
import { IoIosArrowRoundBack } from "react-icons/io";

import PrimaryButton from "@/components/ui/button/primary_button.tsx";

export default function RightAuth({
  children,
}: {
  children?: ReactNode;
}): ReactElement {
  return (
    <div className={"md:flex-7/12 flex flex-col"}>
      <div className={"flex justify-end px-5 py-8 lg:px-[100px] lg:py-[48px]"}>
        <PrimaryButton
          className={"border-primary bg-transparent"}
          type={"button"}
        >
          <Link
            className={"text-primary  flex gap-3 items-center"}
            href={"/menu"}
          >
            <IoIosArrowRoundBack className={"text-primary"} size={28} />
            <span>Back to Shop</span>
          </Link>
        </PrimaryButton>
      </div>
      {children}
    </div>
  );
}
