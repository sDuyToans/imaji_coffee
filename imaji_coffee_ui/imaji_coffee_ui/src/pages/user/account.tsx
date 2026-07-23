import { ReactElement } from "react";

import DefaultLayout from "@/layouts/default.tsx";
import AccountHeader from "@/components/ui/account/account_header.tsx";
import UserDetail from "@/components/ui/account/user_detail.tsx";
import AddressList from "@/components/ui/account/address_list.tsx";
import OrderHistory from "@/components/ui/account/order_history.tsx";

export default function Account(): ReactElement {
  return (
    <DefaultLayout>
      <AccountHeader
        content={"Here you can manage your account, address and order"}
        title={"My Account"}
      />
      <AccountContent />
    </DefaultLayout>
  );
}

function AccountContent(): ReactElement {
  return (
    <div
      className={
        "px-5 py-[48px] lg:px-[124px] lg:py-[80px] flex flex-col gap-[48px] lg:flex-row"
      }
    >
      {" "}
      <AccountDetail />
      <OrderHistory />
    </div>
  );
}

function AccountDetail(): ReactElement {
  return (
    <div className={"lg:flex-4/12 flex flex-col gap-8"}>
      <UserDetail />
      <AddressList />
    </div>
  );
}
