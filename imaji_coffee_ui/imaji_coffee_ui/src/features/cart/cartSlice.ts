// import { createSelector, createSlice, PayloadAction } from "@reduxjs/toolkit";
//
// import { ProductItem } from "@/types";
// import { RootState } from "@/store/store.ts";
//
// export interface Promo {
//   code: string;
//   discountType: "percentage" | "fixed" | "free_shipping";
//   discountValue: number; // % for percentage, $ for fixed, 0 for free_shipping
// }
//
// export interface CartItem extends ProductItem {
//   cartQuantity: number;
// }
//
// interface CartState {
//   items: CartItem[];
//   promo: Promo | null;
//   shipping: {
//     methodId: number | null;
//     price: number;
//   };
// }
//
// function getCartKey(username: string | null) {
//   return `cart_${username || "guest"}`;
// }
//
// function getUsernameFromToken(): string | null {
//   const token = localStorage.getItem("token");
//
//   if (!token) return null;
//
//   try {
//     const payload = JSON.parse(atob(token.split(".")[1]));
//
//     return payload.username || payload.sub || null;
//   } catch {
//     return null;
//   }
// }
//
// const loadCart = (username: string | null): CartState => {
//   try {
//     const stored = localStorage.getItem(getCartKey(username));
//
//     return stored
//       ? JSON.parse(stored)
//       : { items: [], promo: null, shipping: { methodId: null, price: 0 } };
//   } catch {
//     return { items: [], promo: null, shipping: { methodId: null, price: 0 } };
//   }
// };
//
// const initialUsername = getUsernameFromToken();
// const initialState: CartState = loadCart(initialUsername);
//
// const cartSlice = createSlice({
//   name: "cart",
//   initialState,
//   reducers: {
//     addToCart: (
//       state,
//       action: PayloadAction<{ product: ProductItem; quantity: number }>,
//     ) => {
//       const { product, quantity } = action.payload;
//
//       const existing = state.items.find(
//         (item) => item.productId === product.productId,
//       );
//
//       if (existing) {
//         existing.cartQuantity += quantity;
//       } else {
//         state.items.push({ ...product, cartQuantity: quantity });
//       }
//     },
//
//     removeFromCart: (state, action: PayloadAction<number>) => {
//       state.items = state.items.filter(
//         (item) => item.productId !== action.payload,
//       );
//     },
//
//     updateQuantity: (
//       state,
//       action: PayloadAction<{ productId: number; quantity: number }>,
//     ) => {
//       const item = state.items.find(
//         (item) => item.productId === action.payload.productId,
//       );
//
//       if (item) item.cartQuantity = action.payload.quantity;
//     },
//
//     clearCart: (state) => {
//       state.items = [];
//     },
//
//     applyPromo: (state, action: PayloadAction<Promo>) => {
//       state.promo = action.payload;
//     },
//
//     removePromo: (state) => {
//       state.promo = null;
//     },
//
//     setShippingMethod: (
//       state,
//       action: PayloadAction<{ methodId: number; price: number }>,
//     ) => {
//       state.shipping.methodId = action.payload.methodId;
//       state.shipping.price = action.payload.price;
//     },
//
//     clearShipping: (state) => (state.shipping = { methodId: null, price: 0 }),
//     loadCartForUser: (state, action: PayloadAction<string | null>) => {
//       return loadCart(action.payload);
//     },
//   },
// });
//
// // persist cart after save to local storage - middleware
// export const persistCart = (store: any) => (next: any) => (action: any) => {
//   const result = next(action);
//   const state = store.getState();
//
//   const username = getUsernameFromToken();
//
//   localStorage.setItem(getCartKey(username), JSON.stringify(state.cart));
//
//   return result;
// };
//
// export const selectCartItem = (state: RootState) => state.cart.items;
//
// export const selectCartCount = (state: RootState) =>
//   state.cart?.items
//     ? state.cart.items.reduce((sum, item) => sum + item.cartQuantity, 0)
//     : 0;
//
// export const selectCartSummary = createSelector(
//   (state: RootState) => state.cart.items,
//   (state: RootState) => state.cart.promo,
//   (state: RootState) => state.cart.shipping,
//   (items, promo, shipping) => {
//     const subtotal = items.reduce(
//       (sum, i) => sum + i.price * i.cartQuantity,
//       0,
//     );
//     const tax = subtotal * 0.1;
//
//     let discount = 0;
//     let hasFreeShipping = false;
//
//     if (promo) {
//       if (promo.discountType === "fixed") {
//         discount = promo.discountValue;
//       } else if (promo.discountType === "percentage") {
//         discount = subtotal * (promo.discountValue / 100);
//       } else if (promo.discountType === "free_shipping") {
//         hasFreeShipping = true;
//       }
//     }
//
//     const shippingCost = hasFreeShipping ? 0 : shipping.price;
//     const total = subtotal + tax + shippingCost - discount;
//
//     return {
//       subtotal,
//       tax,
//       discount,
//       shipping: shippingCost,
//       promoCode: promo?.code ?? null,
//       promoType: promo?.discountType ?? null,
//       hasFreeShipping,
//       total,
//     };
//   },
// );
//
// export const {
//   addToCart,
//   removeFromCart,
//   updateQuantity,
//   clearCart,
//   applyPromo,
//   removePromo,
//   setShippingMethod,
//   loadCartForUser,
// } = cartSlice.actions;
//
// export const cartReducer = cartSlice.reducer;
