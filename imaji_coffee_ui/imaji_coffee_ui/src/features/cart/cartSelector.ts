import { CartItem } from "@/types";

export interface CartSummary {
  subtotal: number;
  tax: number;
  discount: number;
  shipping: number;
  promoCode: string | null;
  promoType: string | null;
  hasFreeShipping: boolean;
  total: number;
}
export function calculateCartSummary(
  items: CartItem[],
  shippingPrice: number,
  promo: { code: string; discountType: string; discountValue: number } | null,
) {
  const subtotal: number = items.reduce(
    (sum, i) => sum + i.price * i.quantity,
    0,
  );
  const tax = subtotal * 0.1;

  let discount = 0;
  let hasFreeShipping = false;

  if (promo) {
    if (promo.discountType === "fixed") {
      discount = promo.discountValue;
    } else if (promo.discountType === "percentage") {
      discount = subtotal * (promo.discountValue / 100);
    } else {
      hasFreeShipping = true;
    }
  }

  const shippingCost = hasFreeShipping ? 0 : shippingPrice;
  const total = subtotal + tax + shippingCost - discount;

  return {
    subtotal,
    tax,
    discount,
    shippingCost,
    total,
    hasFreeShipping,
    promoCode: promo?.code ?? null,
  };
}
