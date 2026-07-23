import { ReactElement } from "react";
import { Link } from "@heroui/link";
import { Image } from "@heroui/image";

import { Icon } from "@/components/layouts/icons.tsx";

export default function Footer(): ReactElement {
  return (
    <div
      className={
        "w-full mx-auto bg-black text-white px-[20px] py-[48px] lg:py-[56px]"
      }
    >
      <div
        className={
          "flex flex-col gap-[80px] lg:px-[100px] items-center justify-center"
        }
      >
        <TopFooter />
        <BotFooter />
      </div>
    </div>
  );
}

function BotFooter(): ReactElement {
  return (
    <div
      className={
        "w-full mx-auto flex items-center justify-between text-lg text-primary"
      }
    >
      <p>© 2023 IMAJI COFFEE, All rights reserved</p>
      <div className={"flex items-center"}>
        <p>Terms and Conditions</p>
        <div className="h-4 border-l border-white mx-4" />
        <p>Privacy Policy</p>
      </div>
    </div>
  );
}

function TopFooter(): ReactElement {
  return (
    <div className={"flex flex-col items-center gap-[32px] w-full"}>
      <h1 className={"text-5xl lg:text-6xl"}>Our Location</h1>
      <div
        className={"flex flex-col gap-3 items-center text-[16px] lg:text-xl"}
      >
        <h5>Jl. Bangkringan No 19, RT.11/RW.2, Kota Surabaya, 60124</h5>
        <h5>
          <b>Customer Service</b> +6282-2876-6862
        </h5>
        <h5>
          <b>We Are Open from</b> Sun - Mon 10 AM - 22 PM
        </h5>
      </div>
      <div
        className={
          "border-t-1 border-light-grey-40 pt-[32px] flex flex-col gap-[32px] lg:flex-row items-center justify-between w-full"
        }
      >
        <div className={"flex items-center gap-[24px]"}>
          <Link href={"#"}>
            <Icon name={"spotify"} />
          </Link>
          <Link href={"#"}>
            <Icon name={"instagram"} />
          </Link>
          <Link href={"#"}>
            <Icon name={"tiktok"} />
          </Link>
          <Link href={"#"}>
            <Icon name={"youtube"} />
          </Link>
          <Link href={"#"}>
            <Icon name={"twitter"} />
          </Link>
          <Link href={"#"}>
            <Icon name={"telegram"} />
          </Link>
        </div>
        <div className={"flex flex-col md:flex-row gap-[24px] items-center"}>
          <h6 className={"text-lg"}>Delivery Order</h6>
          <div className={"flex flex-row gap-6"}>
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
    </div>
  );
}
