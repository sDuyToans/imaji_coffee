import { Option, ProductCategory } from "@/types";

export const tabs: Option[] = [
  { key: ProductCategory.coffee_baverage, label: "Coffee and Beverage" },
  { key: ProductCategory.food_snack, label: "Food and Snack" },
  { key: ProductCategory.at_home, label: "Imaji Coffee at Home" },
];

export const priceSelects: Option[] = [
  {
    key: "all",
    label: "All Items",
  },
  {
    key: "10",
    label: "Under $10",
  },
  {
    key: "6",
    label: "Under $6",
  },
];
export const bestSellingSelects: Option[] = [
  { key: "all", label: "All Items" },
  { key: "best_selling", label: "Best Selling" },
  { key: "price_asc", label: "Price Lower To High" },
  { key: "price_desc", label: "Price High To Low" },
];
