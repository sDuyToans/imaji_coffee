import { ReactElement } from "react";
import {
  Controller,
  Path,
  useController,
  useFormContext,
} from "react-hook-form";
import "react-phone-input-2/lib/style.css";
import ReactPhoneInput from "react-phone-input-2";

import ErrorText from "@/components/ui/erros/error_text.tsx";
import { CheckoutData } from "@/types";

export default function PhoneInput({
  name,
}: {
  name: Path<CheckoutData>;
}): ReactElement {
  const { control } = useFormContext<CheckoutData>();
  const { field, fieldState } = useController({ name, control });
  const safeValue =
    typeof field.value === "string" || typeof field.value === "number"
      ? String(field.value)
      : "";

  return (
    <div className={"flex flex-col gap-1"}>
      <Controller
        control={control}
        name={name}
        render={({ field }) => (
          <ReactPhoneInput
            country={"ca"}
            inputStyle={{ width: "100%" }}
            value={safeValue}
            onChange={(value) => field.onChange(value)}
          />
        )}
      />
      {fieldState.error && <ErrorText message={fieldState.error.message} />}
    </div>
  );
}
