import React, { useState } from "react";
import { Input } from "@heroui/input";

import { INPUT_TYPES } from "@/utils/enums/EnumsType.ts";
import { EyeFilledIcon } from "@/components/ui/custom/eye_filled_icon.tsx";
import { EyeSlashFilledIcon } from "@/components/ui/custom/eye_slash_icon.tsx";

type InputProps = {
  name: string;
  value: string;
  label: string;
  placeHolder?: string;
  input_type: INPUT_TYPES;
  on_change: (e: React.ChangeEvent<HTMLInputElement>) => void;
};

/**
 * Renders a custom input field component with specified properties.
 * @author duytoan
 * @since Mar 05 2026
 *
 * @param {InputProps} props - The properties for configuring the input component.
 * @param {string} props.value - The value to display in the input field.
 * @param {string} props.placeHolder - The placeholder text for the input field.
 * @param {string} props.label - The label text associated with the input field.
 * @return {React.ReactElement} - A React element representing the custom input field.
 */
export default function Input_custom_with_type(
  props: InputProps,
): React.ReactElement {
  const { input_type } = props;

  switch (input_type) {
    case INPUT_TYPES.PASSWORD: {
      return <InputPassword {...props} />;
    }
    default: {
      return <InputTextOrNumber {...props} />;
    }
  }
}

function InputPassword(props: InputProps): React.ReactElement {
  const { label, placeHolder, value, on_change, name } = props;
  const [isVisible, setIsVisible] = useState(false);
  const toggleVisibility = () => setIsVisible(!isVisible);

  return (
    <Input
      classNames={{
        inputWrapper: "rounded-none border border-primary bg-white",
        input: "rounded-none bg-white",
      }}
      endContent={
        <button
          aria-label="toggle password visibility"
          className="focus:outline-solid outline-transparent"
          type="button"
          onClick={toggleVisibility}
        >
          {isVisible ? (
            <EyeSlashFilledIcon className="text-2xl text-default-400 pointer-events-none" />
          ) : (
            <EyeFilledIcon className="text-2xl text-default-400 pointer-events-none" />
          )}
        </button>
      }
      label={label}
      labelPlacement={"outside-top"} // can custom 4 types here
      name={name}
      placeholder={placeHolder ? placeHolder : ""}
      type={isVisible ? "text" : "password"}
      value={value}
      onChange={(e) => on_change(e)}
    />
  );
}

function InputTextOrNumber(props: InputProps): React.ReactElement {
  const { label, placeHolder, value, input_type, name, on_change } = props;

  let i_type = "";

  switch (input_type) {
    case INPUT_TYPES.NUMBER: {
      i_type = "number";
      break;
    }
    default: {
      i_type = "text";
    }
  }

  return (
    <Input
      classNames={{
        inputWrapper: "rounded-none border border-primary bg-white",
        input: "rounded-none bg-white",
      }}
      endContent={<button />}
      label={label}
      labelPlacement={"outside-top"} // can custom 4 types here
      name={name}
      placeholder={placeHolder ? placeHolder : ""}
      type={i_type}
      value={value}
      onChange={(e) => on_change(e)}
    />
  );
}
