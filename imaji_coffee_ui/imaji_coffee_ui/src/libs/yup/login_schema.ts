import * as yup from "yup";

export const loginSchema = yup.object({
  loginInput: yup
    .string()
    .required("Email is required")
    .min(3, "Must be at least 3 characters"),
  password: yup
    .string()
    .required("Password is required")
    .min(6, "Must be at least 6 character"),
});
