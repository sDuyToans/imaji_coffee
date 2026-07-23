import { ReactElement, useEffect, useState } from "react";

import { AddressResponseDto } from "@/types";
import Input_custom_with_type from "@/components/ui/custom/input_custom_with_type.tsx";
import { INPUT_TYPES } from "@/utils/enums/EnumsType.ts";
import PrimaryButton from "@/components/ui/button/primary_button.tsx";

interface AddressFormProps {
  address?: AddressResponseDto | null;
  isLoading: boolean;
  onCancel: () => void;
  onSubmit: (data: AddressResponseDto) => void;
}

const emptyAddress: AddressResponseDto = {
  addressId: 0,
  userId: 0,
  name: "",
  country: "",
  province: "",
  city: "",
  street: "",
  apartment: "",
  postalCode: "",
  phoneNumber: "",
  isDefault: false,
};

export default function AddressForm({
  address,
  isLoading,
  onCancel,
  onSubmit,
}: AddressFormProps): ReactElement {
  const [form, setForm] = useState<AddressResponseDto>(emptyAddress);

  useEffect(() => {
    setForm(address ? { ...address } : emptyAddress);
  }, [address]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const validate = (): string | null => {
    if (!form.name || form.name.length < 2) return "Name is required";
    if (!form.country) return "Country is required";
    if (!form.city) return "City is required";
    if (!form.street || form.street.length < 5) return "Street is required";
    if (!form.postalCode) return "Postal code is required";
    if (!form.phoneNumber) return "Phone number is required";

    return null;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const error = validate();

    if (error) {
      alert(error);

      return;
    }
    onSubmit(form);
  };

  return (
    <form className={"flex flex-col gap-4"} onSubmit={handleSubmit}>
      <Input_custom_with_type
        input_type={INPUT_TYPES.TEXT}
        label={"Name"}
        name={"name"}
        on_change={handleChange}
        value={form.name}
      />
      <div className={"grid grid-cols-1 lg:grid-cols-2 gap-4"}>
        <Input_custom_with_type
          input_type={INPUT_TYPES.TEXT}
          label={"Country"}
          name={"country"}
          on_change={handleChange}
          value={form.country}
        />
        <Input_custom_with_type
          input_type={INPUT_TYPES.TEXT}
          label={"Province/State"}
          name={"province"}
          on_change={handleChange}
          value={form.province}
        />
      </div>
      <div className={"grid grid-cols-1 lg:grid-cols-2 gap-4"}>
        <Input_custom_with_type
          input_type={INPUT_TYPES.TEXT}
          label={"City"}
          name={"city"}
          on_change={handleChange}
          value={form.city}
        />
        <Input_custom_with_type
          input_type={INPUT_TYPES.TEXT}
          label={"Postal Code"}
          name={"postalCode"}
          on_change={handleChange}
          value={form.postalCode}
        />
      </div>
      <Input_custom_with_type
        input_type={INPUT_TYPES.TEXT}
        label={"Street"}
        name={"street"}
        on_change={handleChange}
        value={form.street}
      />
      <Input_custom_with_type
        input_type={INPUT_TYPES.TEXT}
        label={"Apartment/Suite (optional)"}
        name={"apartment"}
        on_change={handleChange}
        value={form.apartment ?? ""}
      />
      <Input_custom_with_type
        input_type={INPUT_TYPES.TEXT}
        label={"Phone Number"}
        name={"phoneNumber"}
        on_change={handleChange}
        value={form.phoneNumber}
      />
      <label className={"flex items-center gap-2 cursor-pointer"}>
        <input
          checked={form.isDefault}
          name="isDefault"
          type="checkbox"
          onChange={handleChange}
        />
        <span>Set as default address</span>
      </label>
      <div className={"flex gap-4 pt-4"}>
        <PrimaryButton
          className={"flex-1 bg-transparent border-primary text-primary"}
          content="Cancel"
          type="button"
          onPress={onCancel}
        />
        <PrimaryButton
          className={"flex-1 bg-primary text-white"}
          content={isLoading ? "Saving..." : "Save"}
          disabled={isLoading}
          type="submit"
        />
      </div>
    </form>
  );
}
