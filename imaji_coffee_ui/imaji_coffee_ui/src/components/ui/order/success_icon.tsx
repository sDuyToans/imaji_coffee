import { ReactElement } from "react";

export default function SuccessIcon(): ReactElement {
  return (
    <div className="h-[291.17px] w-full flex items-center relative mx-auto">
      <img
        alt="confetti_component"
        className="w-full md:w-[291.17px] h-full absolute inset-0 m-auto"
        src="/order/Sections/confetti_component.png"
      />
      <img
        alt="Illustration"
        className="w-[83.08px] h-[83.08px] absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2"
        src="/order/Sections/Illustration.png"
      />
      <div
        className={
          "flex flex-col items-center justify-center absolute -bottom-1/12 w-full"
        }
      >
        <h1 className={"text-5xl font-medium text-center"}>
          Thanks for your order!
        </h1>
      </div>
    </div>
  );
}
