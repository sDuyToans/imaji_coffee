import { ReactElement } from "react";

import "react-phone-input-2/lib/style.css";

import CustomerData from "@/components/ui/checkout/address/customer_data.tsx";
import ShippingAddress from "@/components/ui/checkout/address/shipping_address.tsx";

export default function Address(): ReactElement {
  return (
    <div className={"flex flex-col gap-8 lg:gap-10"}>
      <CustomerData />
      <ShippingAddress />
    </div>
  );
}
