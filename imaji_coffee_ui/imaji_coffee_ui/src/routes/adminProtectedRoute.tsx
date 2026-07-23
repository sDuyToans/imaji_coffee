import { JSX, ReactElement } from "react";
import { Spinner } from "@heroui/spinner";
import { Link } from "@heroui/link";

import { useGetMeQuery } from "@/api/account/accountApi.ts";

interface AdminProtectedRouteProps {
  children: JSX.Element;
}

export default function AdminProtectedRoute({
  children,
}: AdminProtectedRouteProps): ReactElement {
  const { data, isLoading, isError } = useGetMeQuery();

  if (isLoading) {
    return <Spinner color={"primary"} />;
  }

  if (isError || !data) {
    return (
      <div className="p-6 text-center">
        <p className="text-sm text-red-600">Unauthorized. Please sign in.</p>
        <Link className="text-primary" href="/sign-in">
          Go to sign in
        </Link>
      </div>
    );
  }

  const isAdmin = data.roles.includes("ROLE_ADMIN");

  if (!isAdmin) {
    return (
      <div className="p-6 text-center">
        <p className="text-sm text-red-600">
          You do not have permission to access AI admin insights.
        </p>
        <Link className="text-primary" href="/account">
          Return to account
        </Link>
      </div>
    );
  }

  return children;
}
