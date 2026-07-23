import { Link } from "@heroui/link";
import { ReactElement } from "react";

import PrimaryButton from "@/components/ui/button/primary_button.tsx";

export default function LeftAuthContainer(): ReactElement {
  return (
    <div
      className={
        "bg-primary-surface-light h-full flex flex-col items-center gap-6 pt-8 md:flex-5/12  "
      }
    >
      <div className={"px-[100px] py-[48px] flex justify-start"}>
        <h4 className={"text-2xl lg:text-4xl"}>
          <span className={"text-secondary "}>IMAJI</span>
          <span className={"text-primary"}> Coffee.</span>
        </h4>
      </div>
      <div className={"px-8  lg:px-[96px]"}>
        <div className={"flex flex-col gap-6"}>
          <h5 className={"font-medium text-[32px] lg:text-5xl"}>
            We Also Have Coffee For You At Home.
          </h5>
          <p className={"text-base lg:text-xl"}>
            Holidays, work, and parties are great opportunities to serve a
            delicious cup of coffee.
          </p>
          <PrimaryButton
            className={"border-primary bg-transparent"}
            type={"button"}
          >
            <Link className={"text-primary"} href={"/menu"}>
              Order Now
            </Link>
          </PrimaryButton>
        </div>
      </div>
      <img
        alt={"image-1"}
        className={"visible md:hidden w-full object-cover"}
        src={"/auth/Sections/image.png"}
      />
      <img
        alt={"image-3"}
        className={"hidden md:block w-full object-cover"}
        src={"/auth/Sections/image_lg.png"}
      />
    </div>
  );
}
