import { ReactElement, useEffect, useMemo, useState } from "react";
import { Input } from "@heroui/input";
import { Chip } from "@heroui/chip";
import toast from "react-hot-toast";

import PrimaryButton from "@/components/ui/button/primary_button.tsx";
import {
  useApplyPromoCodeMutation,
  useClearPromoMutation,
  useGetCartQuery,
} from "@/api/cart/cartApi.ts";

export default function PromoCodeSection(): ReactElement {
  const { data: cart } = useGetCartQuery();
  const [applyPromoCode, { isLoading: isApplying }] =
    useApplyPromoCodeMutation();
  const [clearPromo, { isLoading: isRemoving }] = useClearPromoMutation();
  const [promoCode, setPromoCode] = useState("");
  const [message, setMessage] = useState("");

  const appliedCode = cart?.promo?.code ?? "";
  const promoValidation = cart?.promoValidation;
  const appliedDiscount = cart?.discount ?? 0;

  useEffect(() => {
    if (!promoCode && appliedCode) {
      setPromoCode(appliedCode);
    }
  }, [appliedCode, promoCode]);

  useEffect(() => {
    if (promoValidation?.message) {
      setMessage(promoValidation.message);
    }
  }, [promoValidation?.message]);

  const canApply = useMemo(() => {
    return promoCode.trim().length > 0 && !isApplying && !isRemoving;
  }, [isApplying, isRemoving, promoCode]);

  const handleApply = async () => {
    try {
      const res = await applyPromoCode(promoCode.trim()).unwrap();
      const appliedMessage =
        res.promoValidation?.message || "Promo code applied successfully";

      setMessage(appliedMessage);
      toast.success(appliedMessage);
    } catch (error) {
      const serverMessage =
        (typeof error === "object" &&
          error !== null &&
          "data" in error &&
          typeof (
            error as { data?: { errorMessage?: string; message?: string } }
          ).data?.errorMessage === "string" &&
          (error as { data: { errorMessage: string } }).data.errorMessage) ||
        (typeof error === "object" &&
          error !== null &&
          "data" in error &&
          typeof (error as { data?: { message?: string } }).data?.message ===
            "string" &&
          (error as { data: { message: string } }).data.message) ||
        "Promo code is invalid";

      setMessage(serverMessage);
      toast.error(serverMessage);
    }
  };

  const handleClear = async () => {
    try {
      await clearPromo().unwrap();
      setMessage("Promo code removed");
      toast.success("Promo code removed");
    } catch {
      setMessage("Failed to remove promo code");
      toast.error("Failed to remove promo code");
    }
  };

  return (
    <section
      aria-labelledby={"promo-code-heading"}
      className={"flex flex-col gap-3"}
    >
      <label
        className={"text-sm font-medium text-[#5A5A5A] dark:text-dark-grey-70"}
        htmlFor={"promo-code-input"}
        id={"promo-code-heading"}
      >
        Promo code
      </label>
      <div className={"flex flex-col items-center sm:flex-row gap-2"}>
        <Input
          aria-describedby={"promo-code-message"}
          classNames={{
            inputWrapper:
              "rounded-xl py-5.5 border border-[#D9CEC1] bg-white data-[focus=true]:border-primary",
            input: "text-sm",
          }}
          id={"promo-code-input"}
          placeholder={"Enter promo code"}
          value={promoCode}
          onChange={(e) => setPromoCode(e.target.value.toUpperCase())}
        />
        <PrimaryButton
          className={
            "px-4 py-2 bg-primary text-white disabled:opacity-60 rounded-2xl"
          }
          disabled={!canApply}
          type={"button"}
          onPress={handleApply}
        >
          {isApplying ? "Applying..." : "Apply"}
        </PrimaryButton>
      </div>

      {appliedCode && (
        <Chip
          color={appliedDiscount !== 0 ? "warning" : "default"}
          variant={"flat"}
          onClose={handleClear}
        >
          {appliedCode}
          {appliedDiscount !== 0 && ` · -$${appliedDiscount}`}
        </Chip>
      )}

      <p
        aria-live={"polite"}
        className={"text-xs text-[#6B7280] min-h-[18px]"}
        id={"promo-code-message"}
        role={"status"}
      >
        {message}
      </p>
    </section>
  );
}
