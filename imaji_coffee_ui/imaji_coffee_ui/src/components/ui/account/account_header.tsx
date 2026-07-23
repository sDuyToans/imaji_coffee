import { ReactElement } from "react";

export default function AccountHeader({
  title,
  content,
}: {
  title: string;
  content: string;
}): ReactElement {
  return (
    <div
      className={
        "bg-primary-surface-light w-dvw flex justify-center flex-col items-center px-5 py-[48px] lg:px-[124px] lg:py-[80px]"
      }
    >
      <h1 className={"text-6xl lg:text-8xl font-medium"}>{title}</h1>
      <p className={"text-base lg:text-xl text-center"}>{content}</p>
    </div>
  );
}
