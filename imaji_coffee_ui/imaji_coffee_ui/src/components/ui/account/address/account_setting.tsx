import React, { ReactElement, useEffect, useState } from "react";
import { IoArrowBack } from "react-icons/io5";
import { useNavigate } from "react-router-dom";
import { Spinner } from "@heroui/spinner";
import toast from "react-hot-toast";
import { useDispatch } from "react-redux";

import DefaultLayout from "@/layouts/default.tsx";
import AccountHeader from "@/components/ui/account/account_header.tsx";
import PrimaryButton from "@/components/ui/button/primary_button.tsx";
import {
  useChangePasswordMutation,
  useGetUserInfoQuery,
  useUpdateProfileMutation,
} from "@/api/account/accountApi.ts";
import { useLogoutMutation } from "@/api/auth/authApi.ts";
import { clearUser } from "@/features/auth/authSlice.ts";
import { apiSlice } from "@/api/jwt/apiSlice.ts";
import { cartApiBE } from "@/api/cart/cartApi.ts";
import { UserDto } from "@/types";
import Input_custom_with_type from "@/components/ui/custom/input_custom_with_type.tsx";
import { INPUT_TYPES } from "@/utils/enums/EnumsType.ts";

const initialPasswordForm = {
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
};

export default function AccountSetting(): ReactElement {
  return (
    <DefaultLayout>
      <AccountHeader
        content={"Here you can update your account"}
        title={"Account Setting"}
      />
      <AccountSettingContent />
    </DefaultLayout>
  );
}

function AccountSettingContent(): ReactElement {
  const navigate = useNavigate();

  return (
    <div
      className={
        "px-5 py-[48px] lg:px-[124px] lg:py-[80px] flex flex-col gap-[48px]"
      }
    >
      <div
        className={
          "flex flex-col lg:flex-row gap-6 lg:gap-8 items-start lg:items-center"
        }
      >
        <PrimaryButton type={"button"} onPress={() => navigate(-1)}>
          <IoArrowBack />
          <span>Back</span>
        </PrimaryButton>
        <p className={"font-medium text-4xl lg:text-5xl"}>Change Account</p>
      </div>
      <div className={"w-full flex justify-center"}>
        <SettingForm />
      </div>
    </div>
  );
}

function SettingForm(): ReactElement {
  const { data, isLoading, isError, refetch } = useGetUserInfoQuery();
  const [updateProfile, { isLoading: isUpdating }] = useUpdateProfileMutation();

  const [profile, setProfile] = useState<UserDto>({
    userId: 0,
    username: "",
    email: "",
    phone: "",
  });

  useEffect(() => {
    if (data) {
      setProfile(data);
    }
  }, [data]);

  const onChangeProfile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    setProfile((prev) => ({ ...prev, [name]: value }));
  };

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await updateProfile({
        username: profile.username,
        email: profile.email,
        phone: profile.phone,
      }).unwrap();
      toast.success("Profile updated successfully");
      await refetch();
    } catch (error) {
      toast.error(extractApiError(error));
    }
  };

  if (isLoading) return <Spinner color={"primary"} />;

  if (isError) {
    return (
      <div className={"flex flex-col gap-4 items-center"}>
        <p>Failed to load profile.</p>
        <PrimaryButton type={"button"} onPress={() => refetch()}>
          Retry
        </PrimaryButton>
      </div>
    );
  }

  return (
    <div
      className={
        "border border-light-grey-40 p-6 lg:p-8 flex flex-col gap-6 lg:w-[570px] w-full"
      }
    >
      <form className={"flex flex-col gap-6"} onSubmit={handleUpdateProfile}>
        <div>
          <Input_custom_with_type
            input_type={INPUT_TYPES.TEXT}
            label={"Name"}
            name={"username"}
            on_change={onChangeProfile}
            value={profile.username}
          />
        </div>
        <div>
          <Input_custom_with_type
            input_type={INPUT_TYPES.TEXT}
            label={"Email"}
            name={"email"}
            on_change={onChangeProfile}
            value={profile.email}
          />
        </div>
        <div>
          <Input_custom_with_type
            input_type={INPUT_TYPES.TEXT}
            label={"Phone Number"}
            name={"phone"}
            on_change={onChangeProfile}
            value={profile.phone}
          />
        </div>
        <PrimaryButton
          className={"bg-primary text-white w-full"}
          content={isUpdating ? "Saving..." : "Save Profile"}
          disabled={isUpdating}
          type={"submit"}
        />
      </form>
      <div className={"flex items-center justify-center w-full"}>
        <div className={"flex-1 h-[1px] bg-dark-grey-70"} />
        <p
          className={
            "mx-4 whitespace-nowrap text-dark-grey-70 text-base lg:text-lg"
          }
        >
          Change password
        </p>
        <div className={"flex-1 h-[1px] bg-dark-grey-70"} />
      </div>
      <UpdatePassword />
    </div>
  );
}

function UpdatePassword(): React.ReactElement {
  const [passwordForm, setPasswordForm] = useState(initialPasswordForm);
  const [changePassword, { isLoading }] = useChangePasswordMutation();
  const [logoutApi] = useLogoutMutation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const onChangePasswordForm = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    setPasswordForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const validatePasswordForm = (): string | null => {
    const { currentPassword, newPassword, confirmPassword } = passwordForm;

    if (!currentPassword || !newPassword || !confirmPassword) {
      return "All password fields are required";
    }
    if (newPassword.length < 6) {
      return "New password must be at least 6 characters";
    }
    if (newPassword !== confirmPassword) {
      return "New password and confirmation do not match";
    }
    if (newPassword === currentPassword) {
      return "New password must differ from current password";
    }

    return null;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const error = validatePasswordForm();

    if (error) {
      toast.error(error);

      return;
    }

    try {
      await changePassword(passwordForm).unwrap();
      toast.success(
        "Your password was changed successfully. Please sign in again.",
        { duration: 5000 },
      );
      setPasswordForm(initialPasswordForm);
      try {
        await logoutApi().unwrap();
      } catch {
        // Continue clearing local state even if the logout request fails.
      }
      dispatch(clearUser());
      dispatch(apiSlice.util.resetApiState());
      dispatch(cartApiBE.util.resetApiState());
      navigate("/sign-in", { replace: true });
    } catch (err) {
      toast.error(extractApiError(err));
    }
  };

  return (
    <form className={"flex flex-col gap-6"} onSubmit={handleSubmit}>
      <div>
        <Input_custom_with_type
          input_type={INPUT_TYPES.PASSWORD}
          label={"Current Password"}
          name={"currentPassword"}
          on_change={onChangePasswordForm}
          placeHolder={"Enter current password"}
          value={passwordForm.currentPassword}
        />
      </div>
      <div>
        <Input_custom_with_type
          input_type={INPUT_TYPES.PASSWORD}
          label={"New Password"}
          name={"newPassword"}
          on_change={onChangePasswordForm}
          placeHolder={"Enter new password"}
          value={passwordForm.newPassword}
        />
      </div>
      <div>
        <Input_custom_with_type
          input_type={INPUT_TYPES.PASSWORD}
          label={"Confirm New Password"}
          name={"confirmPassword"}
          on_change={onChangePasswordForm}
          placeHolder={"Confirm new password"}
          value={passwordForm.confirmPassword}
        />
      </div>
      <PrimaryButton
        className={"bg-primary text-white w-full"}
        content={isLoading ? "Saving..." : "Change Password"}
        disabled={isLoading}
        type={"submit"}
      />
    </form>
  );
}

function extractApiError(error: unknown): string {
  const data =
    typeof error === "object" && error !== null && "data" in error
      ? (error as { data?: { message?: string } }).data
      : undefined;

  return data?.message ?? "Something went wrong. Please try again.";
}
