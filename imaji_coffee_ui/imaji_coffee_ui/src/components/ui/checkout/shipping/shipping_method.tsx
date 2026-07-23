import { ReactElement } from "react";
import { Spinner } from "@heroui/spinner";
import { useFormContext } from "react-hook-form";

import { useGetShipMethodsQuery } from "@/api/ship_methods/shipMethodsApi.ts";
import { CheckoutData, ShippingMethodItem } from "@/types";
import ErrorText from "@/components/ui/erros/error_text.tsx";
import { useUpdateShippingMutation } from "@/api/cart/cartApi.ts";

export default function ShippingMethod(): ReactElement {
  const { data: shippingMethods, isLoading } = useGetShipMethodsQuery();
  const {
    register,
    watch,
    formState: { errors },
  } = useFormContext<CheckoutData>();
  const [updateShipping] = useUpdateShippingMutation();

  // @ts-ignore
  const selected = watch("shipMethodId");

  const handleSelect = (method: ShippingMethodItem) => {
    updateShipping(method.methodId);
  };

  if (isLoading) {
    return <Spinner color="primary" />;
  }

  return (
    <div className="flex flex-col gap-6 lg:gap-8">
      <h3 className="font-medium text-3xl">Shipping Method</h3>
      <div className="flex flex-col gap-4 lg:gap-6">
        {shippingMethods?.map((s: ShippingMethodItem) => (
          <div
            key={s.methodId}
            className="px-5 pb-5 flex justify-between items-center border-b border-neutral-200"
          >
            <div className="flex items-center gap-6">
              <input
                type={"radio"}
                value={s.methodId}
                {...register("shipMethodId", { required: true })}
                onChange={() => handleSelect(s)}
              />
              <div className="flex flex-col gap-2">
                <p className="text-lg font-medium">{s.methodName}</p>
                <p className="text-base font-normal text-dark-grey-70">
                  {s.expectedArrival}
                </p>
              </div>
            </div>
            <div>${s.price}</div>
          </div>
        ))}
        {errors.shipMethodId?.message && (
          <ErrorText message={"Please select a shipping method"} />
        )}
      </div>
    </div>
  );
}
