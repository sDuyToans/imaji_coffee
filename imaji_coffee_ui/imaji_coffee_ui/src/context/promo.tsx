import { createContext, ReactNode, useContext, useState } from "react";

interface PromoContextType {
  isOpenPromoModal: boolean;
  openPromoModal: () => void;
  closePromoModal: () => void;
}

const PromoContext = createContext<PromoContextType | null>(null);

export function PromoProvider({ children }: { children: ReactNode }) {
  const [isOpenPromoModal, setIsOpenPromoModal] = useState(false);
  const openPromoModal = () => setIsOpenPromoModal(true);
  const closePromoModal = () => setIsOpenPromoModal(false);

  return (
    <PromoContext.Provider
      value={{ isOpenPromoModal, openPromoModal, closePromoModal }}
    >
      {children}
    </PromoContext.Provider>
  );
}

export function usePromo() {
  const promoCtx = useContext(PromoContext);

  if (!promoCtx) {
    throw new Error("useCheckout must be used within a Checkout Provider");
  }

  return promoCtx;
}
