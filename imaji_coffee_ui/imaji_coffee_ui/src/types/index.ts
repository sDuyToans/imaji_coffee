import React from "react";

export interface navItem {
  id: number;
  label: string;
  href: string;
}

export interface imageItem {
  id: number;
  imageUrl: string;
  isMain: boolean;
}

export enum ProductCategory {
  coffee_baverage = "coffee_baverage",
  food_snack = "food_snack",
  at_home = "at_home",
}
export interface ProductItem {
  productId: number;
  name: string;
  description: string;
  price: number;
  oldPrice: number;
  images: imageItem[];
  isAvailableAtWeb: boolean;
  category: ProductCategory;
  quantity: number;
  app_sale_percentage: number;
}

export interface ProductPageResponse {
  items: ProductItem[];
  totalPages: number;
  totalElements: number;
}

export interface SliderItem {
  id: number;
  name: string;
  type: string;
  image: string;
  to?: string;
}

export interface NewItem {
  newId: number;
  title: string;
  description: string;
  image: string;
  createdAt: string;
  time: string;
}

export interface EventItem {
  eventId: number;
  name: string;
  start_date: string;
  duration: string;
  image: string;
}

export interface Option {
  key: string;
  label: string;
}

export interface BreadCrumbItem {
  label: string;
  path?: string;
}

export interface PromoItem {
  promoId: number;
  code: string;
  title: string;
  description: string;
  discountType: "percentage" | "fixed" | "free_shipping";
  discountValue: number;
  startAt: string;
  endAt: string;
  isActive: boolean;
}

export interface PromosProduct {
  productId: number;
  availablePromos: PromoItem[];
  unavailablePromos: PromoItem[];
}

export interface ShippingMethodItem {
  map(
    arg0: (s: ShippingMethodItem) => import("react/jsx-runtime").JSX.Element,
  ): import("react").ReactNode;
  methodId: number;
  methodName: string;
  expectedArrival: string;
  price: number;
  value: "F" | "R" | "E" | "I";
}

export interface ShippingAddress {
  name: string;
  country: string;
  province: string;
  city: string;
  postalCode: string;
  street: string;
  apartment?: string;
  phoneNumber: string;
  isDefault: boolean;
}

export interface CheckoutItem {
  productId: number;
  quantity: number;
  price: number;
}

export interface CheckoutData {
  userId: number | null;
  email: string;
  shipMethodId: number;
  shippingAddress: ShippingAddress;
  paymentMethod: "cod" | "paypal" | "card";
  items: CheckoutItem[];
}

export type ElementErrors = {
  cardNumber: string;
  cardExpiry: string;
  cardCVC: string;
};

export type StripeCheckoutProps = {
  errorMessage: string;
  isProcessing: boolean;
  cardName?: string;
  elementErrors: ElementErrors;
  setElementErrors: React.Dispatch<React.SetStateAction<ElementErrors>>;
  setCardName: React.Dispatch<React.SetStateAction<string>>;
};

export interface CountrySelect {
  code: string;
  name: string;
}

export interface OrderItemRequest {
  productId: number;
  quantity: number;
}

export interface OrderRequest {
  shippingAddress: ShippingAddress;
  couponCode?: string | null;
  idempotencyKey: string;
  shipMethodId: number;
  paymentMethod: string;
  items: OrderItemRequest[];
}

export interface OrderResponse {
  orderId: number;
  status: string;
  paymentStatus?: string | null;
  clientSecret?: string | null;
  subtotalAmount: number;
  taxAmount: number;
  shippingAmount: number;
  discountAmount: number;
  totalAmount: number;
  currency: string;
}

export interface ProductOrderDetail {
  productId: number;
  productName: string;
  productImg: string;
  productCategory: string;
  price: number;
  quantity: number;
}

export interface OrderDetail {
  orderId: number;
  email: string;
  shippingAddress: ShippingAddress;
  status: string;
  paymentStatus?: string | null;
  totalAmount: number;
  items: ProductOrderDetail[];
  taxAmount: number;
  shippingAmount: number;
  discountAmount: number;
  shippingMethod: string;
  paymentMethod: string;
  createdAt: string;
}

export interface CartItem extends ProductItem {
  cartQuantity: number;
}

export interface CartItemRequestDto {
  productId: number;
  quantity: number;
}

export interface CartItemResponseDto {
  cartItemId: number;
  productId: number;
  productName: string;
  productCategory: string;
  price: number;
  quantity: number;
  imageUrl: string;
}

export interface CartDto {
  cartId: number;
  userId: number;
  cartItems: CartItemResponseDto[];
  shipMethod?: ShipMethodDto | null;
  promo?: PromoDto | null;
  promoValidation?: PromoValidationDto | null;
  subtotal: number;
  total: number;
  discount: number;
  shipping: number;
  tax: number;
}

export interface ShipMethodDto {
  methodId: number;
  name: string;
  price: number;
}

export interface PromoDto {
  promoId: number;
  code: string;
  discountType: "percentage" | "fixed" | "free_shipping";
  discountValue: number;
}

export interface PromoValidationDto {
  accepted: boolean;
  message: string;
  promoId?: number | null;
  code?: string | null;
  discountType?: "percentage" | "fixed" | "free_shipping" | string | null;
  discountAmount: number;
  subtotal: number;
  shipping: number;
  tax: number;
  total: number;
  expiresAt?: string | null;
  eligibilityHint?: string | null;
}

export interface AddressResponseDto extends ShippingAddress {
  addressId: number;
  userId: number;
  paymentMethod?: string | null;
  paymentStatus?: string | null;
}

export interface AccountOrderResponseDto {
  orderId: number;
  createdAt: string;
  status: string;
  paymentMethod?: string | null;
  paymentStatus?: string | null;
  items: number;
  amount: number;
}

export interface UserDto {
  userId: number;
  username: string;
  email: string;
  phone: string;
}

export interface UserInfo {
  userId: number;
  username: string;
  email: string;
  roles: string;
}

export interface ChatMessage {
  senderName: string;
  senderType: "USER" | "ADMIN";
  content: string;
}

export interface AdminMetricDto {
  label: string;
  value: string;
  trend: string;
}

export interface AdminProductStatDto {
  productId: number;
  productName: string;
  category: string;
  quantity: number;
}

export interface AdminLowStockDto {
  productId: number;
  productName: string;
  category: string;
  currentStock: number;
  unitPrice: number;
}

export interface AdminPromoUsageDto {
  code: string;
  usageCount: number;
}

export interface AdminAlertDto {
  severity: string;
  title: string;
  description: string;
}

export interface AdminRecommendationDto {
  title: string;
  recommendation: string;
  confidence: string;
}

export interface AdminFeedbackSummaryDto {
  analyzedMessages: number;
  sentiment: string;
  recurringIssues: string[];
  note: string;
}

export interface AdminDashboardSummaryDto {
  disclaimer: string;
  generatedAt: string;
  metrics: AdminMetricDto[];
  popularProducts: AdminProductStatDto[];
  lowStockProducts: AdminLowStockDto[];
  topPromoCodes: AdminPromoUsageDto[];
  riskAlerts: AdminAlertDto[];
  inventoryRecommendations: AdminRecommendationDto[];
  feedbackSummary: AdminFeedbackSummaryDto;
}

export interface AdminAiAskRequest {
  question: string;
}

export interface AdminAiAskResponse {
  answer: string;
  recommendationLabel: string;
  evidence: string[];
  suggestedQuestions: string[];
  generatedAt: string;
}

export interface AdminSuggestedQuestionsResponse {
  questions: string[];
}
