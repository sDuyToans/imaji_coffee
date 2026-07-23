import type { NavigateOptions } from "react-router-dom";

import { HeroUIProvider } from "@heroui/system";
import { useHref, useNavigate } from "react-router-dom";
import React, { ReactElement } from "react";
import { Toaster } from "react-hot-toast";

declare module "@react-types/shared" {
  interface RouterConfig {
    routerOptions: NavigateOptions;
  }
}

export function HeroUiProvider({
  children,
}: {
  children: React.ReactNode;
}): ReactElement {
  const navigate = useNavigate();

  return (
    <HeroUIProvider navigate={navigate} useHref={useHref}>
      {children}
      <Toaster
        position={"top-right"}
        toastOptions={{
          className: "rounded-none border border-primary bg-white text-black",
          duration: 4000,
        }}
      />
    </HeroUIProvider>
  );
}
