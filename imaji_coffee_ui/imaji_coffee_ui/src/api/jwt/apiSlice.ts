import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

const apiURL = import.meta.env.VITE_API_BASE_URL;

const baseQuery = fetchBaseQuery({
  baseUrl: apiURL,
  credentials: "include",
});

export const apiSlice = createApi({
  reducerPath: "api",
  baseQuery,
  endpoints: () => ({}),
  tagTypes: ["Orders", "Addresses", "User"],
});

export const apiCartSlice = createApi({
  reducerPath: "apiCart",
  baseQuery,
  tagTypes: ["Cart"],
  endpoints: () => ({}),
});
