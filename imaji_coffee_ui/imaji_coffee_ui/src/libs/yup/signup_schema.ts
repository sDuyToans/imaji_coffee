import * as yup from "yup";
export const signupSchema = yup.object({
  email: yup.string().email().required("Email is required"),
  username: yup
    .string()
    .required("Username is required")
    .min(6, "Must be at least 6 characters")
    .max(25, "Must be smaller or equal to 25 characters"),
  password: yup
    .string()
    .required("Password is required")
    .min(6, "Must be at least 6 character"),
});
