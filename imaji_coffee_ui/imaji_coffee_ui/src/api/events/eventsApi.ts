import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

import { EventItem } from "@/types";

const apiURL = import.meta.env.VITE_API_BASE_URL;

export const eventsApi = createApi({
  reducerPath: "eventsApi",
  baseQuery: fetchBaseQuery({
    baseUrl: apiURL,
  }),
  endpoints: (builder) => ({
    getUpComingEvent: builder.query<EventItem[], void>({
      query: () => "/events/upcoming",
      keepUnusedDataFor: 300, // cache for 5 minutes
    }),
    getClosedEvent: builder.query<EventItem[], void>({
      query: () => "/events/closed",
      keepUnusedDataFor: 300,
    }),
  }),
});

export const { useGetUpComingEventQuery, useGetClosedEventQuery } = eventsApi;
