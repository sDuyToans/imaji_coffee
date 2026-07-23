import { ReactElement, ReactNode } from "react";
import {
  ModalContent,
  Modal as ModalHeroUi,
  ModalBody,
  ModalFooter,
} from "@heroui/modal";
import { Button } from "@heroui/button";

interface CustomModalProps {
  children: ReactNode;
  cancelText: string;
  confirmText: string;
  onConfirm?: () => void;
  isOpen: boolean;
  onClose: () => void;
  haveFooter?: boolean;
  styles?: string;
  size?:
    | "sm"
    | "xs"
    | "md"
    | "lg"
    | "xl"
    | "2xl"
    | "3xl"
    | "4xl"
    | "5xl"
    | "full";
}

export default function Modal({
  children,
  cancelText,
  confirmText,
  onConfirm,
  onClose,
  isOpen,
  haveFooter = true,
  styles,
  size,
}: CustomModalProps): ReactElement {
  return (
    <ModalHeroUi
      backdrop="opaque"
      className={`rounded-none ${styles}`}
      classNames={{
        backdrop:
          "bg-linear-to-t from-zinc-900 to-zinc-900/10 backdrop-opacity-20",
      }}
      isOpen={isOpen}
      size={size ?? "xl"}
      onOpenChange={onClose}
    >
      <ModalContent>
        {(close): ReactElement => (
          <>
            <ModalBody>{children}</ModalBody>
            <ModalFooter className={"flex items-center gap-4 justify-center"}>
              {haveFooter && (
                <>
                  <Button
                    className={"rounded-none border border-[#E3E3E3]"}
                    variant="light"
                    onPress={close}
                  >
                    {cancelText}
                  </Button>
                  <Button
                    className={"rounded-none"}
                    color="primary"
                    onPress={onConfirm}
                  >
                    {confirmText}
                  </Button>
                </>
              )}
            </ModalFooter>
          </>
        )}
      </ModalContent>
    </ModalHeroUi>
  );
}
