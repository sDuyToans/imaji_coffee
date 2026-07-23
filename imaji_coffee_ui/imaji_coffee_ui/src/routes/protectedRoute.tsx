import { JSX, ReactElement, useEffect, useState } from "react";
import { Spinner } from "@heroui/spinner";
import { Navigate } from "react-router-dom";

import { LoginStatus } from "@/utils/enums/EnumsType.ts";

interface ProtectedRouteProps {
  children: JSX.Element;
}

/**
 * @author duytoan
 * @since 10/2025
 * @param children
 * @return children elements if the user logged in
 */
export default function ProtectedRoute({
  children,
}: ProtectedRouteProps): ReactElement {
  const [status, setStatus] = useState<LoginStatus>(LoginStatus.LOADING);

  useEffect(() => {
    (async () => {
      try {
        const res = await fetch("http://localhost:8080/api/v1/account/me", {
          credentials: "include",
        });

        setStatus(res.ok ? LoginStatus.OK : LoginStatus.FAIL);
      } catch {
        setStatus(LoginStatus.FAIL);
      }
    })();
  }, []);

  if (status == LoginStatus.LOADING) return <Spinner color={"primary"} />;
  if (status == LoginStatus.FAIL)
    return <Navigate replace={true} to={"/sign-in"} />;

  return children;
}
