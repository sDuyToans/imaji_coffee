import { ReactElement } from "react";
import { Image } from "@heroui/image";
import { Link } from "@heroui/link";

import PageHeading from "@/components/ui/page_heading.tsx";

export default function HomeCoffeeApp(): ReactElement {
  return (
    <div className={"flex flex-col gap-[48px] md:gap-[56px]"}>
      <HomeAppCarousel />
      <GetTheApp />
      <img
        alt={"get the app"}
        className={"w-dvw"}
        src={"/home/coffee_app/Sections/Image.png"}
      />
    </div>
  );
}

function HomeAppCarousel(): ReactElement {
  return (
    <div className={"p-[20px] lg:p-[124px] flex flex-col gap-6 px-[20px]"}>
      <div className={"flex gap-1 items-center"}>
        <Image
          alt={"monogram"}
          classNames={{
            wrapper: "bg-primary rounded-lg w-full h-full",
          }}
          src={"/logo/Sections/MONOGRAM.png"}
        />
        <h4 className={"text-[25px] lg:text-[37px]"}>
          <span className={"text-secondary "}>IMA</span>
          <span className={"text-primary"}>COF</span>
        </h4>
      </div>
      <PageHeading
        className={"text-left"}
        title={"The Best Experience Enjoying Coffee"}
      />
      <div className={"flex flex-col md:flex-row gap-6 md:justify-between"}>
        <p className={"md:text-left text-base md:text-md lg:text-xl flex-1"}>
          Enjoy a cup of coffee full of flavor from Imaji Coffee in just one
          application.
        </p>
        <p className={"md:text-left text-base md:text-md lg:text-xl flex-1"}>
          Order without the wait, enjoy daily exclusive deals, earn Imaji
          Points, and more exciting promos!
        </p>
      </div>
    </div>
  );
}

function GetTheApp(): ReactElement {
  return (
    <div
      className={
        "flex flex-col gap-[16px] md:gap-6 md:flex-row items-center justify-center p-[20px] lg:p-[124px] w-[98dvw]"
      }
    >
      <p className={"text-base font-normal"}>Get the App</p>
      <div className={"flex gap-[24px] items-center"}>
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
