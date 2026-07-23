import { ReactElement, useEffect } from "react";
import { Spinner } from "@heroui/spinner";
import { useNavigate } from "react-router-dom";

/**
 * @author duytoan
 * @since 10/30/2025
 */
export default function AuthCallback(): ReactElement {
  const navigate = useNavigate();

  useEffect(() => {
    navigate("/", { replace: true });
  }, [navigate]);

  return <Spinner color={"primary"} />;
}
