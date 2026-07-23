import * as yup from "yup";
export const checkoutSchema = yup.object({
  userId: yup.number().notRequired(),
  email: yup.string().email("Invalid email").required("Email is require"),
  shippingAddress: yup.object({
    name: yup.string().required("Name is required"),
    apartment: yup.string().optional(),
    country: yup.string().required("Country is require"),
    province: yup.string().required("Province is require"),
    city: yup.string().required("City is require"),
    postalCode: yup.string().required("Postal Code is required"),
    street: yup.string().required("Street detail is require"),
    phoneNumber: yup
      .string()
      .matches(/^[0-9]+$/, "Phone must be numbers only")
      .min(10, "Must be at least 10 digits")
      .required("Phone Number is required"),
    isDefault: yup.boolean().notRequired().default(false),
  }),
  shipMethodId: yup
    .number()
    .min(1, "Please select a shipping method")
    .required("Shipping method is required"),
  paymentMethod: yup
    .mixed<"cod" | "paypal" | "card">()
    .oneOf(["cod", "paypal", "card"])
    .required("Payment method is required"),
  items: yup.array().of(
    yup.object({
      productId: yup.number().required("Product ID is required"),
      quantity: yup.number().required("Cart quantity is required").min(1),
    }),
  ),
});
