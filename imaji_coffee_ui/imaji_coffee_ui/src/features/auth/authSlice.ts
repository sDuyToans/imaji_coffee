import { createSlice, PayloadAction } from "@reduxjs/toolkit";

type AuthState = {
  isAuthenticated: boolean;
  username: string | null;
  isLoading: boolean;
};
const initialState: AuthState = {
  isAuthenticated: false,
  username: null,
  isLoading: false,
};

const authSlice = createSlice({
  name: "auth",
  initialState: initialState,
  reducers: {
    setAuthLoading: (state, action: PayloadAction<boolean>) => {
      state.isLoading = action.payload;
    },
    setUser: (state, action: PayloadAction<{ username: string }>) => {
      state.isAuthenticated = true;
      state.username = action.payload.username;
      state.isLoading = false;
    },
    clearUser: (state) => {
      state.isAuthenticated = false;
      state.username = null;
      state.isLoading = false;
    },
  },
});

export const { setUser, clearUser, setAuthLoading } = authSlice.actions;

export const authReducer = authSlice.reducer;
