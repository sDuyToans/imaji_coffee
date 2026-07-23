import { apiCartSlice } from "@/api/jwt/apiSlice.ts";
import { CartDto, CartItemRequestDto, CartItemResponseDto } from "@/types";

export const cartApiBE = apiCartSlice.injectEndpoints({
  endpoints: (builder) => ({
    getCart: builder.query<CartDto, void>({
      query: () => "/cart",
      providesTags: ["Cart"],
    }),
    addToCart: builder.mutation<CartItemResponseDto, CartItemRequestDto>({
      query: (body) => ({
        url: "/cart/items",
        method: "POST",
        body,
      }),
      invalidatesTags: ["Cart"],
    }),
    updateCart: builder.mutation<
      CartItemResponseDto,
      { cartItemId: number; quantity: number }
    >({
      query: ({ cartItemId, quantity }) => ({
        url: `/cart/items/${cartItemId}`,
        method: "PATCH",
        body: { quantity },
      }),
      invalidatesTags: ["Cart"],
    }),
    removeFromCart: builder.mutation<void, number>({
      query: (cartItemId) => ({
        url: `/cart/items/${cartItemId}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Cart"],
    }),
    clearCart: builder.mutation<void, void>({
      query: () => ({
        url: "/cart",
        method: "DELETE",
      }),
      invalidatesTags: ["Cart"],
    }),
    //  shipping update endpoint
    updateShipping: builder.mutation<void, number>({
      query: (shipId) => ({
        url: `/cart/shipping/${shipId}`,
        method: "PATCH",
      }),
      invalidatesTags: ["Cart"],
    }),
    //  promo update endpoint
    updatePromo: builder.mutation<CartDto, number>({
      query: (promoId) => ({
        url: `/cart/promo/${promoId}`,
        method: "PATCH",
      }),
      invalidatesTags: ["Cart"],
    }),
    applyPromoCode: builder.mutation<CartDto, string>({
      query: (code) => ({
        url: "/cart/promo/apply",
        method: "PATCH",
        body: { code },
      }),
      invalidatesTags: ["Cart"],
    }),
    clearPromo: builder.mutation<void, void>({
      query: () => ({
        url: `/cart/promo`,
        method: "DELETE",
      }),
      invalidatesTags: ["Cart"],
    }),
    clearShipping: builder.mutation<void, void>({
      query: () => ({
        url: "/cart/clearShip",
        method: "POST",
      }),
      invalidatesTags: ["Cart"],
    }),
  }),
});

export const {
  useAddToCartMutation,
  useClearCartMutation,
  useGetCartQuery,
  useUpdateCartMutation,
  useRemoveFromCartMutation,
  useUpdatePromoMutation,
  useApplyPromoCodeMutation,
  useUpdateShippingMutation,
  useClearPromoMutation,
  useClearShippingMutation,
} = cartApiBE;
