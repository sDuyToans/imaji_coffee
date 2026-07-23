import { useGetProductsQuery } from "@/api/products/productsApi.ts";

export function useProducts(
  category: string,
  page: number,
  search: string,
  price: string,
  sort: string,
) {
  const mappedMaxPrice = price === "10" ? 10 : price === "6" ? 6 : undefined;

  return useGetProductsQuery({
    category,
    page,
    size: 8,
    search,
    maxPrice: mappedMaxPrice,
    sort,
  });
}
