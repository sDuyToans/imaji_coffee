import React, { ReactElement } from "react";

import { Navbar } from "@/components/layouts/navbar.tsx";
import Footer from "@/components/layouts/footer.tsx";
import SupportWidget from "@/components/ui/support/support_widget.tsx";

export default function DefaultLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>): ReactElement {
  return (
    <div className="relative flex flex-col h-screen">
      <Navbar />
      <main className="mx-auto w-full">{children}</main>
      <Footer />
      <SupportWidget />
    </div>
  );
}
