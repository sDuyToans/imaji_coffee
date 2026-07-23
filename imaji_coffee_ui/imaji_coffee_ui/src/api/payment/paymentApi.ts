import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

const apiURL = import.meta.env.VITE_API_BASE_URL;

export const paymentApi = createApi({
  reducerPath: "paymentApi",
  baseQuery: fetchBaseQuery({
    baseUrl: apiURL,
  }),
  endpoints: (builder) => ({
    createPaymentIntent: builder.mutation<
      { clientSecret: string },
      { amount: number; currency: string }
    >({
      query: (body) => ({
        url: "/payment/create-payment-intent",
        method: "POST",
        body,
      }),
    }),
  }),
});

export const { useCreatePaymentIntentMutation } = paymentApi;
