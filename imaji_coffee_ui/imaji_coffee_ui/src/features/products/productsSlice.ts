import { createSlice } from "@reduxjs/toolkit";

import { ProductItem } from "@/types";

export interface ProductsState {
  products: ProductItem[];
}

const initialState: ProductsState = {
  products: [],
};

//  not used cause at this point we only fetch products from the api

export const productsSlice = createSlice({
  name: "products",
  initialState,
  reducers: {},
});

export default productsSlice.reducer;
