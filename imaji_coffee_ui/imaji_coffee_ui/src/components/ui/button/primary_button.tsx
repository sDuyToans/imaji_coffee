import { ReactElement, ReactNode } from "react";
import { Button } from "@heroui/button";
import clsx from "clsx";

interface Props {
  content?: string;
  onPress?: () => void;
  type: "button" | "submit" | "reset";
  className?: string;
  children?: ReactNode;
  disabled?: boolean;
}

export default function PrimaryButton(props: Props): ReactElement {
  const {
    content,
    onPress,
    type,
    className: styles,
    children,
    disabled,
  } = props;
  const baseClasses: string =
    "text-primary border border-primary w-[164px] h-[48px] bg-transparent rounded-none " +
    "dark:text-white dark:border-white";

  if (disabled)
    return (
      <Button
        className={clsx(
          baseClasses,
          styles,
          disabled &&
            "cursor-not-allowed opacity-50 pointer-events-none select-none transition-none",
        )}
        type={type}
      >
        {content}
        {children}
      </Button>
    );

  return (
    <Button className={clsx(baseClasses, styles)} type={type} onPress={onPress}>
      {content}
      {children}
    </Button>
  );
}
