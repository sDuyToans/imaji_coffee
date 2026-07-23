import { ReactElement, type ReactNode } from "react";
import {
  Drawer,
  DrawerBody,
  DrawerContent,
  DrawerFooter,
} from "@heroui/drawer";

interface CustomDrawerProps {
  title?: string;
  children: ReactNode;
  isOpen: boolean;
  onClose: () => void;
  footer?: ReactNode;
  placement?: "left" | "right" | "top" | "bottom";
}

export default function DrawerUI({
  children,
  isOpen,
  onClose,
  footer,
  placement,
}: CustomDrawerProps): ReactElement {
  return (
    <Drawer
      className="!w-[95%] max-w-[95%] rounded-none p-0 z-999"
      disableAnimation={false}
      hideCloseButton={false}
      isOpen={isOpen}
      placement={placement}
      onClose={onClose}
      onOpenChange={onClose}
    >
      <DrawerContent className={"z-999"}>
        {() => (
          <>
            <DrawerBody className={"p-0 z-999"}>{children}</DrawerBody>
            {footer && <DrawerFooter>{footer}</DrawerFooter>}
          </>
        )}
      </DrawerContent>
    </Drawer>
  );
}
