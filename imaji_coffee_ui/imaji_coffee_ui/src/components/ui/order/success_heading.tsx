import { ReactElement } from "react";

export default function SuccessHeading({
  email,
}: {
  email: string;
}): ReactElement {
  return (
    <div className={"flex flex-col items-center justify-center"}>
      <p className={"text-base text-center"}>
        The order confirmation has been sent to{" "}
        <span className={"text-primary"}>{email}</span>
      </p>
    </div>
  );
}
