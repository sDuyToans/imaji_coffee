import { FcGoogle } from "react-icons/fc";

import PrimaryButton from "@/components/ui/button/primary_button.tsx";

const apiURL = import.meta.env.VITE_BACKEND_BASE_URL;

export default function GoogleLoginOrRegister({
  content,
}: {
  content: string;
}) {
  const handleLoginGoggle = () => {
    window.location.href = `${apiURL}/oauth2/authorization/google`;
  };

  return (
    <PrimaryButton
      className={"border-[#717171] w-full text-center flex gap-3 items-center"}
      type={"button"}
      onPress={handleLoginGoggle}
    >
      <FcGoogle size={32} />
      <span>{content}</span>
    </PrimaryButton>
  );
}
