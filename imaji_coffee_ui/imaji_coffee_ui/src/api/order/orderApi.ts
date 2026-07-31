import { apiSlice } from "@/api/jwt/apiSlice.ts";
import { OrderDetail, OrderRequest, OrderResponse } from "@/types";

export const orderApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    createOrder: builder.mutation<OrderResponse, OrderRequest>({
      query: (orderData) => ({
        url: "/order",
        method: "POST",
        body: orderData,
      }),
      invalidatesTags: ["Orders"],
    }),
    createOrderForPayPal: builder.mutation<OrderResponse, OrderRequest>({
      query: (orderData) => ({
        url: "/order/paypal",
        method: "POST",
        body: orderData,
      }),
      invalidatesTags: ["Orders"],
    }),
    getOrderById: builder.query<OrderDetail, number | string>({
      query: (orderId) => `/order/${orderId}`,
      providesTags: ["Orders"],
    }),
    updateOrderStatus: builder.mutation<
      OrderResponse,
      { orderId: number; status: string }
    >({
      query: ({ orderId, status }) => ({
        url: `/order/${orderId}/status`,
        method: "PATCH",
        body: { status },
      }),
      invalidatesTags: ["Orders"],
    }),
    cancelOrder: builder.mutation<OrderResponse, number>({
      query: (orderId) => ({
        url: `/order/${orderId}/cancel`,
        method: "POST",
      }),
      invalidatesTags: ["Orders"],
    }),
    confirmStripePayment: builder.mutation<OrderResponse, number>({
      query: (orderId) => ({
        url: `/order/${orderId}/confirm-stripe-payment`,
        method: "POST",
      }),
      invalidatesTags: ["Orders"],
    }),
  }),
});

export const {
  useCreateOrderMutation,
  useGetOrderByIdQuery,
  useUpdateOrderStatusMutation,
  useCreateOrderForPayPalMutation,
  useCancelOrderMutation,
  useConfirmStripePaymentMutation,
} = orderApi;
