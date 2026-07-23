import { ReactElement } from "react";
import {
  CardCvcElement,
  CardExpiryElement,
  CardNumberElement,
} from "@stripe/react-stripe-js";
import { Spinner } from "@heroui/spinner";
import { useMemo, useState } from "react";

import { ElementErrors, StripeCheckoutProps } from "@/types";
import ErrorText from "@/components/ui/erros/error_text.tsx";

export default function StripeCheckout({
  elementErrors,
  setElementErrors,
  isProcessing,
  setCardName,
  cardName,
  errorMessage,
}: StripeCheckoutProps): ReactElement {
  const [focusedField, setFocusedField] = useState<
    "cardNumber" | "cardExpiry" | "cardCVC" | null
  >(null);
  const [cardBrand, setCardBrand] = useState("card");
  const [numberComplete, setNumberComplete] = useState(false);
  const [expiryComplete, setExpiryComplete] = useState(false);
  const [cvcComplete, setCvcComplete] = useState(false);
  const [previewNumber, setPreviewNumber] = useState("•••• •••• •••• ••••");
  const [previewExpiry, setPreviewExpiry] = useState("MM/YY");
  const [previewCvc, setPreviewCvc] = useState("•••");

  const labelStyle =
    "block text-lg font-semibold text-primary dark:text-light mb-2";
  const fieldBaseClass =
    "h-[42px] w-full px-4 py-2 text-base border rounded-md transition border-primary dark:border-light focus:ring focus:ring-dark dark:focus:ring-lighter focus:outline-none text-gray-800 dark:text-lighter bg-white dark:bg-gray-600 placeholder-gray-400 dark:placeholder-gray-300";
  const fieldErrorClass =
    "border-red-400 dark:border-red-500 focus:ring-red-500";
  const fieldValidClass =
    "border-primary dark:border-light focus:ring-dark dark:focus:ring-lighter";

  const getClassForElements = (field: keyof ElementErrors) =>
    `${fieldBaseClass} ${elementErrors[field] ? fieldErrorClass : fieldValidClass}`;

  const elementOptions = {
    style: {
      base: {
        fontSize: "16px",
        color: "#000",
        backgroundColor: "#FFF",
      },
      invalid: {
        color: "#F87171",
        backgroundColor: "#FFF",
      },
    },
  };

  const brandLabel = useMemo(() => {
    if (!cardBrand || cardBrand === "unknown" || cardBrand === "card") {
      return "CARD";
    }

    return cardBrand.toUpperCase();
  }, [cardBrand]);

  function formatNumberPreview(rawValue: string): string {
    const digits = rawValue.replace(/\D/g, "").slice(0, 16);

    if (!digits.length) {
      return "•••• •••• •••• ••••";
    }

    const masked = digits
      .padEnd(16, "•")
      .replace(/(.{4})/g, "$1 ")
      .trim();

    return masked;
  }

  function handleCardChange(
    field: "cardNumber" | "cardExpiry" | "cardCVC",
    event: any,
  ) {
    if (event?.brand) {
      setCardBrand(event.brand);
    }
    if (field === "cardNumber") {
      setNumberComplete(!!event?.complete);
      if (typeof event?.value === "string") {
        setPreviewNumber(formatNumberPreview(event.value));
      } else if (event?.empty) {
        setPreviewNumber("•••• •••• •••• ••••");
      }
    }
    if (field === "cardExpiry") {
      setExpiryComplete(!!event?.complete);
      if (typeof event?.value === "string" && event.value.trim().length > 0) {
        setPreviewExpiry(event.value);
      } else if (event?.empty) {
        setPreviewExpiry("MM/YY");
      } else if (event?.complete) {
        setPreviewExpiry("••/••");
      }
    }
    if (field === "cardCVC") {
      setCvcComplete(!!event?.complete);
      if (typeof event?.value === "string" && event.value.trim().length > 0) {
        setPreviewCvc("•".repeat(event.value.trim().length));
      } else if (event?.empty) {
        setPreviewCvc("•••");
      } else if (event?.complete) {
        setPreviewCvc("•••");
      }
    }

    setElementErrors((prev) => ({
      ...prev,
      [field]: event.error ? event.error.message : "",
    }));
  }

  return (
    <div>
      <Spinner
        className={isProcessing ? "visible" : "hidden"}
        color={"primary"}
      >
        Processing payment... Don&#39;t refresh the page
      </Spinner>
      {errorMessage && <ErrorText message={errorMessage} />}
      <div
        className={`${isProcessing ? "hidden" : "visible"} mb-10 px-1 sm:px-3`}
      >
        <div className="mx-auto w-full max-w-[480px] [perspective:1000px]">
          <div
            className="relative h-60 w-full rounded-3xl shadow-[0_15px_45px_rgba(0,0,0,0.35)] transition-transform duration-500 [transform-style:preserve-3d]"
            style={{
              transform:
                focusedField === "cardCVC"
                  ? "rotateY(180deg)"
                  : "rotateY(0deg)",
            }}
          >
            <div className="absolute inset-0 overflow-hidden rounded-3xl bg-gradient-to-br from-[#1a2a5f] via-[#263a88] to-[#4f72d4] p-6 text-white [backface-visibility:hidden]">
              <div className="pointer-events-none absolute -right-12 -top-12 h-40 w-40 rounded-full bg-white/15 blur-2xl" />
              <div className="pointer-events-none absolute -left-8 bottom-0 h-36 w-36 rounded-full bg-black/20 blur-2xl" />
              <div className="flex items-start justify-between">
                <p className="text-sm font-semibold tracking-[0.28em] text-white/90">
                  {brandLabel}
                </p>
                <span className="rounded-full border border-white/40 bg-white/10 px-3 py-1 text-xs">
                  Secure
                </span>
              </div>
              <div
                className={`mt-9 rounded-xl px-4 py-3 text-2xl tracking-[0.25em] transition ${
                  focusedField === "cardNumber"
                    ? "bg-white/30 ring-2 ring-white/70"
                    : "bg-white/20"
                }`}
              >
                <div className="flex items-center justify-between gap-3">
                  <p className="whitespace-nowrap">{previewNumber}</p>
                  <span
                    className={`rounded-full px-2.5 py-1 text-xs ${
                      numberComplete
                        ? "bg-emerald-400/90 text-black"
                        : "bg-white/20"
                    }`}
                  >
                    {numberComplete ? "✓" : "••"}
                  </span>
                </div>
              </div>
              <div className="mt-8 flex items-end justify-between text-sm">
                <div
                  className={`rounded-lg px-3 py-2 transition ${
                    focusedField === "cardExpiry"
                      ? "bg-white/30"
                      : "bg-white/10"
                  }`}
                >
                  <div className="flex items-center gap-2">
                    <p className="text-[10px] uppercase tracking-wider text-white/80">
                      Expires
                    </p>
                    <span
                      className={`rounded-full px-2 py-0.5 text-[10px] ${
                        expiryComplete
                          ? "bg-emerald-400/90 text-black"
                          : "bg-white/20"
                      }`}
                    >
                      {expiryComplete ? "✓" : ".."}
                    </span>
                  </div>
                  <p className="mt-1 text-base">{previewExpiry}</p>
                </div>
                <div className="rounded-lg bg-white/10 px-3 py-2 text-right">
                  <p className="text-[10px] uppercase tracking-wider text-white/80">
                    Cardholder
                  </p>
                  <p className="mt-1 max-w-[220px] truncate text-base">
                    {cardName?.trim() ? cardName : "YOUR NAME"}
                  </p>
                </div>
              </div>
            </div>

            <div
              className="absolute inset-0 rounded-3xl bg-gradient-to-br from-[#191919] via-[#111] to-[#050505] text-white [backface-visibility:hidden]"
              style={{ transform: "rotateY(180deg)" }}
            >
              <div className="mt-8 h-12 w-full bg-black/70" />
              <div className="px-6 pt-7">
                <p className="mb-2 text-xs uppercase tracking-[0.2em] text-white/75">
                  Security code
                </p>
                <div
                  className={`rounded-lg bg-white px-4 py-3 text-right font-mono tracking-[0.28em] text-black ${
                    focusedField === "cardCVC" ? "ring-2 ring-white/70" : ""
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <p>{previewCvc}</p>
                    <span
                      className={`rounded-full px-2.5 py-1 text-xs ${
                        cvcComplete
                          ? "bg-emerald-400 text-black"
                          : "bg-gray-200 text-gray-700"
                      }`}
                    >
                      {cvcComplete ? "✓" : "..."}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <p className="mt-3 text-center text-xs text-gray-500 dark:text-gray-400">
            Secure preview: Stripe does not expose raw card details to the page.
          </p>
        </div>
      </div>
      <div
        className={`${isProcessing ? "hidden" : "visible"} flex flex-col gap-5`}
      >
        <div className="grid grid-cols-1 gap-5 lg:grid-cols-2">
          <div>
            <label className={labelStyle} htmlFor="cardNumber">
              Card Number
            </label>
            <CardNumberElement
              className={getClassForElements("cardNumber")}
              options={elementOptions}
              onBlur={() => setFocusedField(null)}
              onChange={(event) => handleCardChange("cardNumber", event)}
              onFocus={() => setFocusedField("cardNumber")}
            />
            {elementErrors.cardNumber && (
              <ErrorText message={elementErrors.cardNumber} />
            )}
          </div>
          <div>
            <label className={labelStyle} htmlFor="cardNumber">
              Card Holder Name
            </label>
            <input
              className="h-[42px] w-full rounded-md border border-primary px-3 py-2 text-base focus:outline-none focus:ring focus:ring-dark"
              onChange={(e) => setCardName(e.target.value)}
            />
          </div>
        </div>
        <div className="grid grid-cols-1 gap-5 lg:grid-cols-2">
          <div>
            <label className={labelStyle} htmlFor="cardNumber">
              Card Expiry
            </label>
            <CardExpiryElement
              className={getClassForElements("cardExpiry")}
              options={elementOptions}
              onBlur={() => setFocusedField(null)}
              onChange={(event) => handleCardChange("cardExpiry", event)}
              onFocus={() => setFocusedField("cardExpiry")}
            />
            {elementErrors.cardExpiry && (
              <ErrorText message={elementErrors.cardExpiry} />
            )}
          </div>
          <div>
            <label className={labelStyle} htmlFor="cardNumber">
              Card CVC
            </label>
            <CardCvcElement
              className={getClassForElements("cardCVC")}
              options={elementOptions}
              onBlur={() => setFocusedField(null)}
              onChange={(event) => handleCardChange("cardCVC", event)}
              onFocus={() => setFocusedField("cardCVC")}
            />
            {elementErrors.cardCVC && (
              <ErrorText message={elementErrors.cardCVC} />
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
