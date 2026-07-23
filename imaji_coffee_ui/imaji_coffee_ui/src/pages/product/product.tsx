import { ReactElement, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Spinner } from "@heroui/spinner";

import DefaultLayout from "@/layouts/default.tsx";
import { useGetProductByProductIdQuery } from "@/api/products/productsApi.ts";
import Detail from "@/components/ui/detail/detail.tsx";
import MayInterest from "@/components/ui/product/interest.tsx";

export default function Product(): ReactElement {
  const { productId } = useParams<{ productId: string }>();
  const {
    data: product,
    isLoading,
    error,
  } = useGetProductByProductIdQuery(Number(productId));

  // Scroll to top when page loads
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "instant" });
  }, [productId]);

  if (isLoading) {
    return (
      <DefaultLayout>
        <div className="flex justify-center items-center min-h-[400px]">
          <Spinner color={"primary"} />
        </div>
      </DefaultLayout>
    );
  }

  if (error || !product) {
    return (
      <DefaultLayout>
        <div className="flex justify-center items-center min-h-[400px]">
          <p>Failed to load product</p>
        </div>
      </DefaultLayout>
    );
  }

  return (
    <DefaultLayout>
      <div className="px-5 lg:px-[124px]">
        <Detail product={product} />
      </div>
      <MayInterest category={product.category} excludedId={product.productId} />
    </DefaultLayout>
  );
}
