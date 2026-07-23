import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

import { NewItem } from "@/types";
const apiURL = import.meta.env.VITE_API_BASE_URL;

export const newsApi = createApi({
  reducerPath: "newsApi",
  baseQuery: fetchBaseQuery({
    baseUrl: apiURL,
  }),
  endpoints: (builder) => ({
    getNews: builder.query<NewItem[], void>({
      query: () => "/news",
    }),
    getNewById: builder.query<NewItem, string | number>({
      query: (newId) => `/${newId}`,
    }),
  }),
});

export const { useGetNewsQuery, useGetNewByIdQuery } = newsApi;
