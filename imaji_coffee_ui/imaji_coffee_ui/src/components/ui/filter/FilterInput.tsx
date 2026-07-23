import { Input } from "@heroui/input";
import { CiSearch } from "react-icons/ci";
import { ReactElement } from "react";

interface Props {
  label: string;
  value: string;
  onChange: (val: string) => void;
}

export default function FilterInput({
  label,
  value,
  onChange,
}: Props): ReactElement {
  return (
    <div className="flex flex-col gap-[8px] flex-1">
      <div className="flex justify-between text-base items-center">
        <span className="font-medium">{label}</span>
        {value && (
          <button
            className="text-sm text-primary dark:text-white hover:underline transition-all"
            onClick={() => onChange("")}
          >
            Clear
          </button>
        )}
      </div>
      <Input
        classNames={{
          inputWrapper:
            "rounded-none bg-white dark:bg-gray-800 border-gray-300 dark:border-gray-700",
          input: "rounded-none",
        }}
        placeholder="Search products..."
        startContent={<CiSearch className="text-gray-400" />}
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
      />
    </div>
  );
}
