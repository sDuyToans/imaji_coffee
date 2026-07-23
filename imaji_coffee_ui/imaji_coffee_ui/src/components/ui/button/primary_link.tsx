import { ReactElement } from "react";
import { Link } from "@heroui/link";

export default function PrimaryLink({
  to,
  content,
}: {
  to: string;
  content: string;
}): ReactElement {
  return (
    <Link
      className={
        "rounded-none bg-primary flex items-center justify-center text-white w-[171px] h-[44px] md:w-[190px]"
      }
      href={to}
    >
      <span>{content}</span>
    </Link>
  );
}
