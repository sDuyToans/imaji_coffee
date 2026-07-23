import { Radio, RadioGroup } from "@heroui/radio";
import { useFormContext } from "react-hook-form";

import StripeCheckout from "@/components/ui/checkout/checkout_method/stripe_checkout.tsx";
import { CheckoutData, StripeCheckoutProps } from "@/types";
import PaypalCheckout from "@/components/ui/checkout/checkout_method/paypal_checkout.tsx";

export default function PaymentMethod({
  elementErrors,
  setElementErrors,
  isProcessing,
  setCardName,
  cardName,
  errorMessage,
}: StripeCheckoutProps) {
  const { watch, setValue } = useFormContext<CheckoutData>();

  const paymentMethod = watch("paymentMethod");

  const handlePaymentChange = (val: "card" | "paypal" | "cod") => {
    setValue("paymentMethod", val, { shouldValidate: true });
  };

  return (
    <div className={"flex flex-col gap-6 lg:gap-8"}>
      <h3 className={"font-medium text-3xl"}>Payment Method</h3>
      <RadioGroup
        className="flex flex-col gap-4"
        value={paymentMethod}
        onValueChange={(val) =>
          handlePaymentChange(val as "card" | "paypal" | "cod")
        }
      >
        <div
          className={
            "flex flex-col gap-4 px-4 py-3 border-b border-neutral-200"
          }
        >
          <div className={"flex gap-4 items-center"}>
            <Radio value="card" />
            <span className="text-lg font-medium">Debit/Credit Card</span>
          </div>
          {paymentMethod === "card" && (
            <StripeCheckout
              cardName={cardName}
              elementErrors={elementErrors}
              errorMessage={errorMessage}
              isProcessing={isProcessing}
              setCardName={setCardName}
              setElementErrors={setElementErrors}
            />
          )}
        </div>

        {/* PayPal */}
        <div
          className={
            "flex flex-col gap-4 px-4 py-3 border-b border-neutral-200"
          }
        >
          <div className={"flex gap-4 items-center"}>
            <Radio value="paypal" />
            <span className="text-lg font-medium">Paypal</span>
          </div>
          {paymentMethod === "paypal" && <PaypalCheckout />}
        </div>

        {/* COD */}
        <div
          className={
            "flex flex-col gap-4 px-4 py-3 border-b border-neutral-200"
          }
        >
          <div className={"flex gap-4 items-center"}>
            <Radio value="cod" />
            <span className="text-lg font-medium">Cash on Delivery (COD)</span>
          </div>
        </div>
      </RadioGroup>
    </div>
  );
}
