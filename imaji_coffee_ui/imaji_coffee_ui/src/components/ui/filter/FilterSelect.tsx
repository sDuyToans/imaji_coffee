import { Select, SelectItem } from "@heroui/select";
import { ReactElement } from "react";

import { Option } from "@/types";

interface Props {
  label: string;
  value: string;
  onChange: (val: string) => void;
  options: Option[];
}

export default function FilterSelect({
  label,
  value,
  onChange,
  options,
}: Props): ReactElement {
  return (
    <div className="flex flex-col gap-[8px] flex-1">
      <div className="flex justify-between text-base items-center">
        <span className="font-medium">{label}</span>
        {value && value !== "all" && (
          <button
            className="text-sm text-primary dark:text-white hover:underline transition-all"
            onClick={() => onChange("all")}
          >
            Clear
          </button>
        )}
      </div>
      <Select
        aria-label={label}
        classNames={{
          trigger:
            "rounded-none bg-white dark:bg-gray-800 border-gray-300 dark:border-gray-700",
        }}
        selectedKeys={[value]}
        onSelectionChange={(keys) => {
          const [selected] = Array.from(keys);

          onChange(selected as string);
        }}
      >
        {options.map((opt) => (
          <SelectItem key={opt.key}>{opt.label}</SelectItem>
        ))}
      </Select>
    </div>
  );
}
