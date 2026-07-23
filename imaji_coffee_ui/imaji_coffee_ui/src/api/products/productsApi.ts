import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

import { ProductCategory, ProductItem, ProductPageResponse } from "@/types";
const apiURL = import.meta.env.VITE_API_BASE_URL;

export const productsApi = createApi({
  reducerPath: "productsApi",
  baseQuery: fetchBaseQuery({
    baseUrl: apiURL,
  }),
  endpoints: (builder) => ({
    getProductsBySize: builder.query<
      ProductItem[],
      { size: number; category?: ProductCategory }
    >({
      query: ({ size, category }) =>
        `${category ? `/products?category=${category}&size=${size}` : `/products?size=${size}`}`,
    }),
    getProductByProductId: builder.query({
      query: (productId: number) => `/products/${productId}`,
    }),
    getRelatedProducts: builder.query<
      ProductItem[],
      { category: string; size: number; excludedId: number }
    >({
      query: ({ category, size, excludedId }) =>
        `/products/related?category=${category}&size=${size}&excludedId=${excludedId}`,
    }),
    getProducts: builder.query<
      ProductPageResponse,
      {
        category: string;
        page: number;
        size: number;
        search?: string;
        maxPrice?: number;
        sort?: string;
      }
    >({
      query: ({ category, page, size, search, maxPrice, sort }) => {
        const urlSearchParams = new URLSearchParams({
          category,
          page: String(page),
          size: String(size),
        });

        if (search) urlSearchParams.append("search", search);

        if (maxPrice !== undefined) {
          urlSearchParams.append("maxPrice", String(maxPrice));
        }

        if (sort === "best_selling") {
          urlSearchParams.append("sortBy", "quantity");
          urlSearchParams.append("sortDirection", "asc");
        } else if (sort === "price_asc") {
          urlSearchParams.append("sortBy", "price");
          urlSearchParams.append("sortDirection", "asc");
        } else if (sort === "price_desc") {
          urlSearchParams.append("sortBy", "price");
          urlSearchParams.append("sortDirection", "desc");
        } else {
          urlSearchParams.append("sortBy", "createdAt");
          urlSearchParams.append("sortDirection", "asc");
        }

        return `/products/search?${urlSearchParams.toString()}`;
      },

      serializeQueryArgs: ({ endpointName, queryArgs }) => {
        const { category, search = "", maxPrice = "", sort = "" } = queryArgs;

        return `${endpointName}-${category}-${search}-${maxPrice}-${sort}`;
      },

      merge: (currentCache, newResponse, { arg }) => {
        if (!currentCache || arg.page === 0) return newResponse;

        const existingIds = new Set(currentCache.items.map((p) => p.productId));
        const newUniqueItems = newResponse.items.filter(
          (p) => !existingIds.has(p.productId),
        );

        currentCache.items.push(...newUniqueItems);
        currentCache.totalPages = newResponse.totalPages;
        currentCache.totalElements = newResponse.totalElements;
      },
      forceRefetch({ currentArg, previousArg }) {
        return currentArg?.page !== previousArg?.page;
      },
    }),
  }),
});

export const {
  useGetProductsQuery,
  useGetProductsBySizeQuery,
  useGetProductByProductIdQuery,
  useGetRelatedProductsQuery,
} = productsApi;
