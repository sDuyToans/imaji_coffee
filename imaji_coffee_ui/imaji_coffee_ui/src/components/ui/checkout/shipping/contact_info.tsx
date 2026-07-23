import { ReactElement, ReactNode } from "react";
import { useFormContext } from "react-hook-form";

import { CheckoutData } from "@/types";

export default function ContactInfo({
  children,
}: {
  children?: ReactNode;
}): ReactElement {
  const { getValues } = useFormContext<CheckoutData>();

  const customerEmail: string = getValues("email");
  const address = getValues("shippingAddress");
  const fullAddress = [
    address.street,
    address.apartment,
    address.city,
    address.province,
    address.postalCode,
    address.country,
  ]
    .filter(Boolean)
    .join(", ");

  return (
    <div
      className={
        "flex flex-col gap-6 lg:gap-5 pb-6 border-b border-neutral-200"
      }
    >
      <div
        className={
          "flex flex-col text-base lg:text-xl gap-2 lg:flex-row lg:justify-between lg:gap-5"
        }
      >
        <p className={"font-medium flex-1/5"}>Contact</p>
        <p className={"font-normal flex-4/5"}>{customerEmail}</p>
      </div>
      <div
        className={
          "flex flex-col text-base lg:text-xl gap-2 lg:flex-row lg:justify-between lg:gap-5"
        }
      >
        <p className={"font-medium flex-1/5"}>Ship to</p>
        <p className={"font-normal flex-4/5"}>{fullAddress}</p>
      </div>
      {children}
    </div>
  );
}
