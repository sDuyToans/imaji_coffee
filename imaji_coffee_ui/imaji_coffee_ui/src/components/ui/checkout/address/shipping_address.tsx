import { ReactElement } from "react";
import { useFormContext } from "react-hook-form";
import { Checkbox } from "@heroui/checkbox";

import { CheckoutData } from "@/types";
import ErrorText from "@/components/ui/erros/error_text.tsx";
import CountrySelect from "@/components/ui/checkout/address/country_select.tsx";
import CustomInput from "@/components/layouts/form/custom_input.tsx";
import PhoneInput from "@/components/layouts/form/phone_input.tsx";

export default function ShippingAddress(): ReactElement {
  const {
    register,
    formState: { errors },
  } = useFormContext<CheckoutData>();

  return (
    <div className={"flex flex-col gap-6 lg:gap-8"}>
      <h3 className={"text-3xl font-medium"}>Shipping Address</h3>
      <div className={"flex flex-col gap-4 lg:gap-6"}>
        <div className={"flex flex-col gap-4 lg:flex-row lg:gap-6"}>
          <CustomInput name={"shippingAddress.name"} placeholder={"Name"} />
          <CustomInput
            name={"shippingAddress.apartment"}
            placeholder={"Apartment, suite, etc"}
          />
        </div>
        <div className={"grid grid-cols-2 gap-4 lg:grid-cols-4 lg:gap-6"}>
          <div className={"flex flex-col gap-1"}>
            <CountrySelect name={"shippingAddress.country"} />
            {errors.shippingAddress?.country?.message && (
              <ErrorText message={errors.shippingAddress.country.message} />
            )}
          </div>

          <CustomInput
            name={"shippingAddress.province"}
            placeholder={"Province"}
          />

          <CustomInput name={"shippingAddress.city"} placeholder={"City"} />

          <CustomInput
            name={"shippingAddress.postalCode"}
            placeholder={"Postal Code"}
          />
        </div>

        <CustomInput name={"shippingAddress.street"} placeholder={"Street"} />
        <PhoneInput name={"shippingAddress.phoneNumber"} />
        <Checkbox
          color={"primary"}
          defaultSelected={false}
          {...register("shippingAddress.isDefault")}
        >
          Set as default address
        </Checkbox>
      </div>
    </div>
  );
}
