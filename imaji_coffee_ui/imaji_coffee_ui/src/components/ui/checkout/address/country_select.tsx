import { Controller, useFormContext } from "react-hook-form";
import { Select, SelectItem } from "@heroui/select";

import { countries } from "@/data.ts";

type CountrySelectProps = {
  name: string;
  label?: string;
};

export default function CountrySelect({ name }: CountrySelectProps) {
  const { control } = useFormContext();

  return (
    <Controller
      control={control}
      name={name}
      render={({ field }) => (
        <Select
          aria-label={"country"}
          className="w-full"
          classNames={{
            trigger: "rounded-none border border-primary bg-white",
            listbox: "rounded-none border border-primary bg-white",
            value: "text-black", // text color
          }}
          placeholder="Country"
          selectedKeys={field.value ? [field.value] : []}
          onSelectionChange={(keys) => {
            const value = Array.from(keys)[0] as string;

            field.onChange(value);
          }}
        >
          {countries.map((c) => (
            <SelectItem key={c.code}>{c.name}</SelectItem>
          ))}
        </Select>
      )}
    />
  );
}
