import { configureStore } from "@reduxjs/toolkit";

import { productsApi } from "@/api/products/productsApi.ts";
import { newsApi } from "@/api/news/newsApi.ts";
import { eventsApi } from "@/api/events/eventsApi.ts";
import { spacesApi } from "@/api/spaces/spacesApi.ts";
// import { cartReducer, persistCart } from "@/features/cart/cartSlice.ts";
import { promosApi } from "@/api/promos/promosApi.ts";
import { shipMethodsApi } from "@/api/ship_methods/shipMethodsApi.ts";
import { paymentApi } from "@/api/payment/paymentApi.ts";
import { apiSlice } from "@/api/jwt/apiSlice.ts";
import { authApi } from "@/api/auth/authApi.ts";
import { authReducer } from "@/features/auth/authSlice.ts";
import { cartApiBE } from "@/api/cart/cartApi.ts";

let middlewareList = [
  productsApi.middleware,
  newsApi.middleware,
  eventsApi.middleware,
  spacesApi.middleware,
  promosApi.middleware,
  shipMethodsApi.middleware,
  paymentApi.middleware,
  apiSlice.middleware,
  authApi.middleware,
  cartApiBE.middleware,
];

export const store = configureStore({
  reducer: {
    // cart: cartReducer,
    [productsApi.reducerPath]: productsApi.reducer,
    [newsApi.reducerPath]: newsApi.reducer,
    [eventsApi.reducerPath]: eventsApi.reducer,
    [spacesApi.reducerPath]: spacesApi.reducer,
    [promosApi.reducerPath]: promosApi.reducer,
    [shipMethodsApi.reducerPath]: shipMethodsApi.reducer,
    [paymentApi.reducerPath]: paymentApi.reducer,
    [apiSlice.reducerPath]: apiSlice.reducer,
    [authApi.reducerPath]: authApi.reducer,
    [cartApiBE.reducerPath]: cartApiBE.reducer,
    auth: authReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(middlewareList),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
