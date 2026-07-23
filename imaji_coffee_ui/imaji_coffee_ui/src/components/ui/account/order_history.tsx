import { ReactElement, useState } from "react";

import FilterInput from "@/components/ui/filter/FilterInput.tsx";
import FilterSelect from "@/components/ui/filter/FilterSelect.tsx";
import OrderItemStatus from "@/components/ui/account/order_item_status.tsx";

export interface OrderStatusOption {
  key: string;
  label: string;
}

export const orderStatusOptions: OrderStatusOption[] = [
  { key: "All", label: "All statuses" },
  { key: "PENDING", label: "Waiting to Payment" },
  { key: "PAID", label: "Paid" },
  { key: "PROCESSING", label: "Processed" },
  { key: "SHIPPED", label: "Shipped" },
  { key: "DELIVERED", label: "Delivered" },
  { key: "CANCELLED", label: "Canceled" },
  { key: "REFUNDED", label: "Refunded" },
];

export default function OrderHistory(): ReactElement {
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState(orderStatusOptions[0].key);

  return (
    <div className={"lg:flex-8/12 flex flex-col gap-8"}>
      <h1 className={"text-4xl lg:text-5xl text-left"}>Order History</h1>
      <div className={"flex gap-4 items-center lg:gap-7"}>
        <FilterInput label={"Search"} value={search} onChange={setSearch} />
        <FilterSelect
          label={"Filter status"}
          options={orderStatusOptions}
          value={status}
          onChange={setStatus}
        />
      </div>
      <OrderItemStatus search={search} status={status} />
    </div>
  );
}
