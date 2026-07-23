import { ReactElement } from "react";
import { Input } from "@heroui/input";
import { Path, useController, useFormContext } from "react-hook-form";

import { CheckoutData } from "@/types";
import ErrorText from "@/components/ui/erros/error_text.tsx";

export default function CustomInput({
  name,
  placeholder,
}: {
  name: Path<CheckoutData>;
  placeholder: string;
}): ReactElement {
  const { control } = useFormContext<CheckoutData>();
  const { field, fieldState } = useController({ name, control });

  // Force value to string or empty
  const safeValue =
    typeof field.value === "string" || typeof field.value === "number"
      ? String(field.value)
      : "";

  return (
    <div className={"flex flex-col gap-1"}>
      <Input
        ref={field.ref}
        classNames={{
          inputWrapper: "rounded-none  border border-primary bg-white",
          input: "rounded-none bg-white",
        }}
        name={field.name}
        placeholder={placeholder}
        value={safeValue}
        onBlur={field.onBlur}
        onChange={(e) => field.onChange(e.target.value)}
      />
      {fieldState.error && <ErrorText message={fieldState.error.message} />}
    </div>
  );
}
