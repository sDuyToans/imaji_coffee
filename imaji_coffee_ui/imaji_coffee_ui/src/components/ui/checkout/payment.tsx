import { ReactElement } from "react";
import { useFormContext } from "react-hook-form";

import ContactInfo from "@/components/ui/checkout/shipping/contact_info.tsx";
import PaymentMethod from "@/components/ui/checkout/payment/payment_method.tsx";
import { useGetShipMethodsQuery } from "@/api/ship_methods/shipMethodsApi.ts";
import { StripeCheckoutProps } from "@/types";

export default function Payment({
  elementErrors,
  setElementErrors,
  isProcessing,
  setCardName,
  cardName,
  errorMessage,
}: StripeCheckoutProps): ReactElement {
  const { getValues } = useFormContext();

  const { data: shipMethods } = useGetShipMethodsQuery();

  const method = shipMethods?.find(
    (m) => m.methodId.toString() === getValues("shipMethodId"),
  );

  return (
    <div className={"flex flex-col gap-[32px] lg:gap-[48px]"}>
      <ContactInfo>
        <div
          className={
            "flex flex-col text-base lg:text-xl gap-2 lg:flex-row lg:justify-between lg:gap-5"
          }
        >
          <p className={"font-medium flex-1/5"}>Ship Method</p>
          <p className={"font-normal flex-4/5"}>
            {method?.methodName} - ${method?.price}
          </p>
        </div>
      </ContactInfo>
      <PaymentMethod
        cardName={cardName}
        elementErrors={elementErrors}
        errorMessage={errorMessage}
        isProcessing={isProcessing}
        setCardName={setCardName}
        setElementErrors={setElementErrors}
      />
    </div>
  );
}
