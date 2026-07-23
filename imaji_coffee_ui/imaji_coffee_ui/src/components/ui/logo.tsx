import type { ReactElement } from "react";

export default function Logo(): ReactElement {
  return (
    <div className={"flex items-center"}>
      <h4 className={"text-2xl lg:text-4xl"}>
        <span className={"text-secondary "}>IMAJI</span>
        <span className={"text-primary"}> Coffee.</span>
      </h4>
    </div>
  );
}
