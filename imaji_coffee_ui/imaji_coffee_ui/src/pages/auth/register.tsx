import type { ReactElement } from "react";

import { Input } from "@heroui/input";
import { Link } from "@heroui/link";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import toast from "react-hot-toast";
import { Spinner } from "@heroui/spinner";

import PrimaryButton from "@/components/ui/button/primary_button.tsx";
import RightAuth from "@/components/ui/auth/right_auth.tsx";
import LeftAuthContainer from "@/components/ui/auth/left_auth.tsx";
import { useSignupMutation } from "@/api/auth/authApi.ts";
import { signupSchema } from "@/libs/yup/signup_schema.ts";
import ErrorText from "@/components/ui/erros/error_text.tsx";
import GoogleLoginOrRegister from "@/components/ui/OAuth2/google_login_or_register.tsx";

type FormRegisterType = {
  email: string;
  username: string;
  password: string;
};

export default function Register(): ReactElement {
  return (
    <div className={"flex flex-col lg:flex-row min-h-dvh"}>
      <LeftAuthContainer />
      <RightAuth>
        <RegisterContainer />
      </RightAuth>
    </div>
  );
}
function RegisterContainer(): ReactElement {
  const [signup, { isLoading }] = useSignupMutation();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormRegisterType>({
    resolver: yupResolver(signupSchema),
  });

  const onSubmit = async (data: FormRegisterType) => {
    try {
      await signup(data).unwrap();

      // success notification
      toast.success("Account created successfully!");

      // redirect
      navigate("/account");
    } catch (e) {
      console.log(e);

      toast.error("Sign up failed. Please check your details");
    }
  };

  return (
    <div
      className={
        "px-5 py-8 md:px-8 lg:px-[180px] flex flex-col gap-8 lg:gap-[48px]"
      }
    >
      <div className={"flex flex-col gap-3"}>
        <h1 className={"text-4xl md:text-5xl"}>Create Your Account</h1>
        <p className={"text-base md:text-xl"}>
          Create an account to easily shopping
        </p>
        <GoogleLoginOrRegister content={"Sign up with Google"} />
      </div>
      <div className={"flex items-center justify-center w-full"}>
        <div className={"flex-1 h-[1px] bg-dark-grey-70"} />
        <p
          className={
            "mx-4 whitespace-nowrap text-dark-grey-70 text-base lg:text-lg"
          }
        >
          Or used email
        </p>
        <div className={"flex-1 h-[1px] bg-dark-grey-70"} />
      </div>
      <form
        className={"flex flex-col gap-6 md:gap-7"}
        onSubmit={handleSubmit(onSubmit)}
      >
        <div className={"flex flex-col gap-1"}>
          <Input
            classNames={{
              inputWrapper: "rounded-none  border border-primary bg-white",
              input: "rounded-none bg-white",
            }}
            label={"Name"}
            labelPlacement={"outside-top"}
            placeholder={"Enter your name"}
            type={"text"}
            {...register("username")}
          />
          {errors.username?.message && (
            <ErrorText message={errors.username.message} />
          )}
        </div>
        <div className={"flex flex-col gap-1"}>
          <Input
            classNames={{
              inputWrapper: "rounded-none  border border-primary bg-white",
              input: "rounded-none bg-white",
            }}
            label={"Email"}
            labelPlacement={"outside-top"}
            placeholder={"Enter email"}
            type={"email"}
            {...register("email")}
          />
          {errors.email?.message && (
            <ErrorText message={errors.email.message} />
          )}
        </div>
        <div className={"flex flex-col gap-1"}>
          <Input
            classNames={{
              inputWrapper: "rounded-none  border border-primary bg-white",
              input: "rounded-none bg-white",
            }}
            label={"Password"}
            labelPlacement={"outside-top"}
            placeholder={"Enter password"}
            type={"password"}
            {...register("password")}
          />
          {errors.password?.message && (
            <ErrorText message={errors.password.message} />
          )}
        </div>
        <PrimaryButton
          className={"w-full bg-primary text-white"}
          type={"submit"}
        >
          {isLoading ? <Spinner color={"primary"} /> : "Sign up"}
        </PrimaryButton>
      </form>
      <div className={"flex justify-center gap-3"}>
        <p>Already have an account?</p>
        <Link href={"/sign-in"}>Sign in</Link>
      </div>
    </div>
  );
}
