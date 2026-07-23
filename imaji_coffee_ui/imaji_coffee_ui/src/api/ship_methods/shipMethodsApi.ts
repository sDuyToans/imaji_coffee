import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

import { ShippingMethodItem } from "@/types";
const apiURL = import.meta.env.VITE_API_BASE_URL;

export const shipMethodsApi = createApi({
  reducerPath: "shipMethodsApi",
  baseQuery: fetchBaseQuery({
    baseUrl: apiURL,
  }),
  endpoints: (builder) => ({
    getShipMethods: builder.query<ShippingMethodItem[], void>({
      query: () => "/ship-methods",
    }),
  }),
});

export const { useGetShipMethodsQuery } = shipMethodsApi;
