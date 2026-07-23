import { ReactElement } from "react";
import { useFormContext } from "react-hook-form";
import { Input } from "@heroui/input";
import { Link } from "react-router-dom";
import { Checkbox } from "@heroui/checkbox";

import ErrorText from "@/components/ui/erros/error_text.tsx";
import { CheckoutData } from "@/types";
import { useGetMeQuery } from "@/api/account/accountApi.ts";

export default function CustomerData(): ReactElement {
  const {
    register,
    formState: { errors },
  } = useFormContext<CheckoutData>();
  const { data: myInfo } = useGetMeQuery();
  let email = myInfo?.email || "";

  return (
    <div className={"flex flex-col gap-6 lg:gap-8"}>
      <h3 className={"text-3xl font-medium"}>Customer Data</h3>
      <div className={"flex flex-col gap-2"}>
        <div className={"flex flex-col gap-1"}>
          {email === "" ? (
            <>
              <Input
                classNames={{
                  inputWrapper: "rounded-none  border border-primary bg-white",
                  input: "rounded-none bg-white",
                }}
                label={"Email"}
                labelPlacement={"outside-top"}
                placeholder={"Enter mail"}
                // onChange={(e) => handleEmailChange(e.target.value)}
                {...register("email")}
              />
              {errors.email?.message && (
                <ErrorText message={errors.email.message} />
              )}
              <p className={"text-dark-grey-70 text-base"}>
                Already have an account?{" "}
                <Link className={"text-primary"} to={"/sign-in"}>
                  Log in
                </Link>
              </p>
            </>
          ) : (
            <Input
              classNames={{
                inputWrapper: "rounded-none  border border-primary bg-white",
                input: "rounded-none bg-white",
              }}
              label={"Email"}
              labelPlacement={"outside-top"}
              placeholder={"Enter mail"}
              value={email}
              readOnly={true}
              // onChange={(e) => handleEmailChange(e.target.value)}
              {...register("email")}
            />
          )}
        </div>
      </div>
      <Checkbox color={"primary"} defaultSelected={true}>
        Email with news and offer
      </Checkbox>
    </div>
  );
}
