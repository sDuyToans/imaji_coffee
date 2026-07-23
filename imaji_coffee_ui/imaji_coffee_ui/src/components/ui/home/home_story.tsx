import { ReactElement } from "react";
import { Image } from "@heroui/image";

import PageHeading from "@/components/ui/page_heading.tsx";

export default function HomeStory(): ReactElement {
  return (
    <div
      className={
        "bg-primary-surface-light flex flex-col gap-[24px] dark:text-black"
      }
    >
      <StoryText />
      <ImageList />
    </div>
  );
}

function StoryText(): ReactElement {
  return (
    <div className={"p-[20px] lg:px-[124px]"}>
      <PageHeading
        className={"text-left md:text-center font-medium"}
        title={
          "We Provide Your Space For Your Work or Mini event With Your Favorite coffee."
        }
      />
      <div className={"flex flex-col md:flex-row gap-[24px]"}>
        <p className={"flex-1/2"}>
          Our story begins in 2010 with a simple idea from our founder that the
          most comfortable place to work is anywhere, because ideas are not
          limited by space and time, and the most comfortable place is in a café
          where their favorite coffee is available.
        </p>
        <p className={"flex-1/2"}>
          All the best local coffee bean varieties from throughout Indonesia are
          available with us. Directly from selected farmers, high-quality beans
          are processed and roasted to perfection by ourselves, then passed on
          to our skilled baristas who are passionate about preparing a cup of
          longed-for happiness from home.
        </p>
      </div>
    </div>
  );
}

function ImageList(): ReactElement {
  return (
    <div className={"flex flex-col gap-[24px]"}>
      <div className={"sm:block md:hidden"}>
        <div className={"flex gap-[24px] h-[480px] relative"}>
          <Image
            alt={"image1"}
            classNames={{
              wrapper: "absolute top-0 left-0 w-full h-full",
              img: "object-cover rounded-none w-1/2 h-4/5",
            }}
            src={"/home/Sections/image1.png"}
          />
          <Image
            alt={"image2"}
            classNames={{
              wrapper: "absolute -bottom-20 -right-41 w-full h-full",
              img: "w-full h-full object-cover rounded-none w-1/2 h-4/5",
            }}
            src={"/home/Sections/image2.png"}
          />
        </div>
        <div className={"flex gap-[24px] h-[480px] relative"}>
          <Image
            alt={"image3"}
            classNames={{
              wrapper: "absolute top-0 left-0 w-full h-full",
              img: "w-full h-full object-cover rounded-none w-1/2 h-4/5",
            }}
            src={"/home/Sections/image3.png"}
          />
          <Image
            alt={"image4"}
            classNames={{
              wrapper: "absolute -bottom-20 -right-41 w-full h-full",
              img: "w-full h-full object-cover rounded-none w-1/2 h-4/5 ",
            }}
            src={"/home/Sections/image4.png"}
          />
        </div>
      </div>
      <div
        className={
          "hidden md:flex gap-[32px] w-full h-[620px] relative justify-between"
        }
      >
        <Image
          alt={"image1"}
          classNames={{
            wrapper: "h-full",
            img: "rounded-none h-[400px] object-cover",
          }}
          src={"/home/Sections/image1.png"}
        />
        <Image
          alt={"image2"}
          classNames={{
            wrapper: "h-full",
            img: "rounded-none h-[400px] object-cover mt-[80px]",
          }}
          src={"/home/Sections/image2.png"}
        />
        <Image
          alt={"image3"}
          classNames={{
            wrapper: "h-full",
            img: "rounded-none h-[400px] object-cover mt-[160px]",
          }}
          src={"/home/Sections/image3.png"}
        />
        <Image
          alt={"image4"}
          classNames={{
            wrapper: "h-full",
            img: "rounded-none h-[400px] object-cover mt-[220px]",
          }}
          src={"/home/Sections/image4.png"}
        />
      </div>
    </div>
  );
}
