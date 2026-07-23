import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

import { PromosProduct } from "@/types";
const apiURL = import.meta.env.VITE_API_BASE_URL;

export const promosApi = createApi({
  reducerPath: "promosApi",
  baseQuery: fetchBaseQuery({
    baseUrl: apiURL,
  }),
  endpoints: (builder) => ({
    getPromos: builder.query<PromosProduct, { productId: number }>({
      query: ({ productId }) => `/products/${productId}/promos`,
    }),
  }),
});

export const { useGetPromosQuery } = promosApi;
