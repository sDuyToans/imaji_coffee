import { ReactElement } from "react";

export default function ErrorText({
  message,
}: {
  message: string | undefined;
}): ReactElement {
  return <p className={"text-xs lg:text-sm text-red-500"}>{message}</p>;
}
