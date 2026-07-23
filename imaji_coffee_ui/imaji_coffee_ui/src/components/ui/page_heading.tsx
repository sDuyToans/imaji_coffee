import { ReactElement } from "react";

import PageTitle from "@/components/ui/page_title.tsx";

export default function PageHeading({
  className,
  title,
}: {
  title: string;
  className?: string;
}): ReactElement {
  let styles: string = "";

  if (className) {
    styles = className + " ";
  }

  return (
    <div className={"text-center text-6xl lg:text-8xl " + styles}>
      <PageTitle title={title} />
    </div>
  );
}
