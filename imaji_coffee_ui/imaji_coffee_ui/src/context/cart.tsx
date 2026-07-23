import React, { createContext, ReactNode, useContext, useState } from "react";

interface CartContextType {
  isOpenCart: boolean;
  setIsOpenCart: React.Dispatch<React.SetStateAction<boolean>>;
}

const CartContext = createContext<CartContextType | null>(null);

export function CartProvider({ children }: { children: ReactNode }) {
  const [isOpenCart, setIsOpenCart] = useState(false);

  return (
    <CartContext.Provider value={{ isOpenCart, setIsOpenCart }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart(): CartContextType {
  const cartContext = useContext(CartContext);

  if (!cartContext) {
    throw new Error("useCheckout must be used within a Checkout Provider");
  }

  return cartContext;
}
