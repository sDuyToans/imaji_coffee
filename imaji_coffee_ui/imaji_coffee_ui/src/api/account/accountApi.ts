import { apiSlice } from "@/api/jwt/apiSlice.ts";
import {
  AccountOrderResponseDto,
  AddressResponseDto,
  OrderResponse,
  UserDto,
  UserInfo,
} from "@/types";

export interface UpdateProfileRequest {
  username: string;
  email: string;
  phone: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface AddressRequest {
  name: string;
  country: string;
  province: string;
  city: string;
  street: string;
  apartment?: string;
  postalCode: string;
  phoneNumber: string;
  isDefault: boolean;
}

export const accountApi = apiSlice.injectEndpoints({
  endpoints: (build) => ({
    getAccountOrders: build.query<
      AccountOrderResponseDto[],
      { status?: string } | void
    >({
      query: (arg) => {
        const params = arg?.status
          ? `?status=${encodeURIComponent(arg.status)}`
          : "";

        return `/account/orders${params}`;
      },
      providesTags: ["Orders"],
    }),
    getAddresses: build.query<AddressResponseDto[], void>({
      query: () => "/account/address",
      providesTags: ["Addresses"],
    }),
    getUserInfo: build.query<UserDto, void>({
      query: () => "/account/user",
      providesTags: ["User"],
    }),
    getMe: build.query<UserInfo, void>({
      query: () => "/account/me",
    }),
    updateProfile: build.mutation<UserDto, UpdateProfileRequest>({
      query: (body) => ({
        url: "/account/user",
        method: "PATCH",
        body,
      }),
      invalidatesTags: ["User"],
    }),
    changePassword: build.mutation<{ message: string }, ChangePasswordRequest>({
      query: (body) => ({
        url: "/auth",
        method: "PATCH",
        body,
      }),
    }),
    createAddress: build.mutation<AddressResponseDto, AddressRequest>({
      query: (body) => ({
        url: "/account/address",
        method: "POST",
        body,
      }),
      invalidatesTags: ["Addresses"],
    }),
    updateAddress: build.mutation<
      AddressResponseDto,
      { id: number; data: AddressRequest }
    >({
      query: ({ id, data }) => ({
        url: `/account/address/${id}`,
        method: "PUT",
        body: data,
      }),
      invalidatesTags: ["Addresses"],
    }),
    deleteAddress: build.mutation<void, number>({
      query: (id) => ({
        url: `/account/address/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Addresses"],
    }),
    setDefaultAddress: build.mutation<void, number>({
      query: (id) => ({
        url: `/account/address/${id}/default`,
        method: "PATCH",
      }),
      invalidatesTags: ["Addresses"],
    }),
    cancelOrder: build.mutation<OrderResponse, number>({
      query: (id) => ({
        url: `/account/orders/${id}/cancel`,
        method: "POST",
      }),
      invalidatesTags: ["Orders"],
    }),
  }),
});

export const {
  useGetAccountOrdersQuery,
  useGetAddressesQuery,
  useGetUserInfoQuery,
  useGetMeQuery,
  useLazyGetMeQuery,
  useUpdateProfileMutation,
  useChangePasswordMutation,
  useCreateAddressMutation,
  useUpdateAddressMutation,
  useDeleteAddressMutation,
  useSetDefaultAddressMutation,
  useCancelOrderMutation,
} = accountApi;
