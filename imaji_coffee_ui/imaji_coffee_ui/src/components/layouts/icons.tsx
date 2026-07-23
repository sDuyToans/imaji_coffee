import type { ReactElement } from "react";

type IconName =
  | "spotify"
  | "instagram"
  | "telegram"
  | "tiktok"
  | "twitter"
  | "youtube"
  | "shopping-basket"
  | "btn_play"
  | "arrow-reply"
  | "arrow-share"
  | "airplay-alt"
  | "volume-high";

interface IconProps {
  name: IconName;
  alt?: string;
  size?: number;
  className?: string;
}

export function Icon({
  name,
  alt,
  size = 24,
  className = "",
}: IconProps): ReactElement {
  return (
    <img
      alt={alt ?? `${name}_icon`}
      className={`${className} cursor-pointer`}
      height={size}
      src={`/icons/${name}.svg`}
      width={size}
    />
  );
}
