import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

const apiURL = import.meta.env.VITE_API_BASE_URL;

export const authApi = createApi({
  reducerPath: "authApi",
  baseQuery: fetchBaseQuery({ baseUrl: apiURL, credentials: "include" }),
  endpoints: (builder) => ({
    login: builder.mutation<
      { token: string },
      { loginInput: string; password: string }
    >({
      query: (credentials) => ({
        url: "/auth/login",
        method: "POST",
        body: credentials,
      }),
      transformErrorResponse: (response: { status: number; data: any }) => {
        return response.data;
      },
    }),
    signup: builder.mutation<
      { token: string },
      { email: string; username: string; password: string }
    >({
      query: (body) => ({
        url: "/auth/signup",
        method: "POST",
        body: body,
      }),
    }),
    logout: builder.mutation<void, void>({
      query: () => ({
        url: "/auth/logout",
        method: "POST",
      }),
    }),
  }),
});

export const { useLoginMutation, useSignupMutation, useLogoutMutation } =
  authApi;
