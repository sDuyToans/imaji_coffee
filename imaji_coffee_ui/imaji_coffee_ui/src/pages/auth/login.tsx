import type { ReactElement } from "react";

import { Input } from "@heroui/input";
import { Checkbox } from "@heroui/checkbox";
import { Link } from "@heroui/link";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { useNavigate } from "react-router-dom";
import { Spinner } from "@heroui/spinner";
import toast from "react-hot-toast";
import { useDispatch } from "react-redux";

import LeftAuthContainer from "@/components/ui/auth/left_auth.tsx";
import RightAuth from "@/components/ui/auth/right_auth.tsx";
import PrimaryButton from "@/components/ui/button/primary_button.tsx";
import { useLoginMutation } from "@/api/auth/authApi.ts";
import { loginSchema } from "@/libs/yup/login_schema.ts";
import ErrorText from "@/components/ui/erros/error_text.tsx";
import GoogleLoginOrRegister from "@/components/ui/OAuth2/google_login_or_register.tsx";
import { setAuthLoading, setUser } from "@/features/auth/authSlice.ts";
import { useLazyGetMeQuery } from "@/api/account/accountApi.ts";

type LoginFormValues = {
  loginInput: string;
  password: string;
};

export default function Login(): ReactElement {
  return (
    <div className={"flex flex-col lg:flex-row min-h-dvh"}>
      <LeftAuthContainer />
      <RightAuth>
        <LoginContainer />
      </RightAuth>
    </div>
  );
}

function LoginContainer(): ReactElement {
  const [login, { isLoading, error: errorBE }] = useLoginMutation();
  const validationErrors = (errorBE as { data?: Record<string, string> })?.data;
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [triggerMe] = useLazyGetMeQuery();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: yupResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormValues) => {
    try {
      dispatch(setAuthLoading(true));

      await login(data).unwrap();

      const me = await triggerMe().unwrap();

      console.log("ME:", me);

      dispatch(setUser({ username: me.username }));
      toast.success("Log in successfully");
      navigate("/chat");
    } catch (e) {
      console.log("LOGIN/ME ERROR:", e);
      dispatch(setAuthLoading(false));
      toast.error("Login failed");
    }
  };

  return (
    <div
      className={
        "px-5 py-8 md:px-8 lg:px-[180px] flex flex-col gap-8 lg:gap-[48px]"
      }
    >
      <div className={"flex flex-col gap-3"}>
        <h1 className={"text-4xl md:text-5xl"}>Welcome Back</h1>
        <p className={"text-base md:text-xl"}>
          Enter your credentials to access your account
        </p>
        <GoogleLoginOrRegister content={"Sign in with Google"} />
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
            label={"Email"}
            labelPlacement={"outside-top"}
            placeholder={"Enter email"}
            type={"email"}
            {...register("loginInput")}
          />
          {errors?.loginInput && (
            <ErrorText message={errors.loginInput.message} />
          )}
          {validationErrors?.loginInput && (
            <ErrorText message={validationErrors.loginInput} />
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
          {errors?.password && <ErrorText message={errors.password.message} />}
          {validationErrors?.password && (
            <ErrorText message={validationErrors.password} />
          )}
        </div>
        <div className={"flex justify-between items-center"}>
          <Checkbox defaultChecked={false}>Remember me</Checkbox>
          <Link className={"underline text-[#129BFF]"} href={"#"}>
            Forgot Password?
          </Link>
        </div>
        <PrimaryButton
          className={"w-full bg-primary text-white"}
          disabled={isLoading}
          type={"submit"}
        >
          {isLoading ? <Spinner color={"primary"} /> : "Sign in"}
        </PrimaryButton>
      </form>
      <div className={"flex justify-center gap-3"}>
        <p>Don&#39;t have an account?</p>
        <Link className={"text-primary underline"} href={"/register"}>
          Create an account
        </Link>
      </div>
    </div>
  );
}
