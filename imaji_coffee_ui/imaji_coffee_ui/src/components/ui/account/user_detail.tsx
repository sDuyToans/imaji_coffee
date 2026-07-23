import { ReactElement } from "react";
import { Spinner } from "@heroui/spinner";
import { Link } from "@heroui/link";

import {
  useGetMeQuery,
  useGetUserInfoQuery,
} from "@/api/account/accountApi.ts";

export default function UserDetail(): ReactElement {
  const { data, isLoading, isError, refetch } = useGetUserInfoQuery();
  const { data: me } = useGetMeQuery();

  if (isLoading) return <Spinner color={"primary"} />;

  if (isError || !data) {
    return (
      <div
        className={
          "flex flex-col gap-4 lg:gap-5 border border-[#E3E3E3] p-6 lg:p-8"
        }
      >
        <h3 className={"text-2xl lg:text-3xl"}>Account Detail</h3>
        <p>Failed to load account details.</p>
        <Link className={"text-primary"} href={"#"} onPress={() => refetch()}>
          Retry
        </Link>
      </div>
    );
  }

  const isAdmin = me?.roles?.includes("ROLE_ADMIN") ?? false;

  return (
    <div
      className={
        "flex flex-col gap-6 lg:gap-7 border border-[#E3E3E3] p-6 lg:p-8"
      }
    >
      <h3 className={"text-2xl lg:text-3xl"}>Account Detail</h3>
      <div className={"flex flex-col gap-[12px]"}>
        <p className={"text-xl font-medium"}>{data.username}</p>
        <p className={"text-lg lg:text-xl"}>{data.email}</p>
        <p className={"text-lg lg:text-xl"}>{data.phone}</p>
      </div>
      <Link className={"text-primary"} href={"/account/setting"}>
        Account Setting
      </Link>
      {isAdmin && (
        <Link className={"text-primary"} href={"/admin/ai-insights"}>
          AI Insights Dashboard
        </Link>
      )}
    </div>
  );
}
