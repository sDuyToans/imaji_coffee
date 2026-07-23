import { ReactElement } from "react";

import ContactInfo from "@/components/ui/checkout/shipping/contact_info.tsx";
import ShippingMethod from "@/components/ui/checkout/shipping/shipping_method.tsx";

export default function Shipping({}: {}): ReactElement {
  return (
    <div className={"flex flex-col gap-[32px] lg:gap-[48px]"}>
      <ContactInfo />
      <ShippingMethod />
    </div>
  );
}
