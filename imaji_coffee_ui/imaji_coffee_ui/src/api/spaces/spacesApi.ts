import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

import { SliderItem } from "@/types";

const apiURL = import.meta.env.VITE_API_BASE_URL;

export const spacesApi = createApi({
  reducerPath: "spacesApi",
  baseQuery: fetchBaseQuery({
    baseUrl: apiURL,
  }),
  endpoints: (builder) => ({
    getSpaces: builder.query<SliderItem[], void>({
      query: () => "/spaces",
    }),
  }),
});

export const { useGetSpacesQuery } = spacesApi;
