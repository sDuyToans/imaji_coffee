import { useState } from "react";
import { Controller, useFormContext } from "react-hook-form";
import {
  Combobox,
  ComboboxInput,
  ComboboxButton,
  ComboboxOptions,
  ComboboxOption,
} from "@headlessui/react";
import { ChevronDownIcon } from "@heroicons/react/24/outline";

import { countries } from "@/data.ts";

type CountrySelectProps = {
  name: string;
  label?: string;
};

export default function CountrySelect({ name }: CountrySelectProps) {
  const { control } = useFormContext();
  const [query, setQuery] = useState("");

  const filteredCountries =
    query === ""
      ? countries
      : countries.filter((c) =>
          c.name.toLowerCase().includes(query.toLowerCase()),
        );

  return (
    <Controller
      control={control}
      name={name}
      render={({ field }) => (
        <Combobox
          value={field.value ?? ""}
          onChange={(value) => field.onChange(value || null)}
        >
          <div className="relative w-full">
            <ComboboxInput
              aria-label="Country"
              className="w-full h-10 px-3 border border-primary bg-white text-black placeholder:text-gray-500 focus:outline-none focus:ring-1 focus:ring-primary"
              displayValue={(code: string) =>
                countries.find((c) => c.code === code)?.name ?? ""
              }
              placeholder="Country"
              onChange={(event) => setQuery(event.target.value)}
            />
            <ComboboxButton className="absolute inset-y-0 right-0 flex items-center px-2 text-gray-500 hover:text-primary">
              <ChevronDownIcon className="h-4 w-4" />
            </ComboboxButton>
            <ComboboxOptions className="absolute z-50 mt-1 max-h-60 w-full overflow-auto border border-primary bg-white shadow-lg focus:outline-none">
              {filteredCountries.map((country) => (
                <ComboboxOption
                  key={country.code}
                  value={country.code}
                  className="px-3 py-2 cursor-pointer text-black data-[focus]:bg-primary data-[focus]:text-white"
                >
                  {country.name}
                </ComboboxOption>
              ))}
              {filteredCountries.length === 0 && (
                <div className="px-3 py-2 text-gray-500">No country found</div>
              )}
            </ComboboxOptions>
          </div>
        </Combobox>
      )}
    />
  );
}
