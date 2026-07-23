import { NavbarItem } from "@heroui/navbar";
import { Link } from "@heroui/link";

/**
 * @author duytoan
 * @since 14/01/2026
 * @param isLoggedIn
 * @param username
 * @param onLogout
 */
export default function AuthButtons({
  isLoggedIn,
  username,
  onLogout,
}: {
  isLoggedIn: boolean;
  username: string | null;
  onLogout: () => void;
}) {
  return isLoggedIn ? (
    <>
      <NavbarItem>
        <span className="text-black px-4">
          <Link href={"/account"}>Hi, {username}</Link>
        </span>
      </NavbarItem>
      <NavbarItem>
        <button
          className="flex items-center justify-center text-base dark:bg-primary dark:text-black dark:hover:text-white transition text-primary px-4 py-3 border border-primary hover:cursor-pointer w-[85px] h-[45px] text-center"
          onClick={onLogout}
        >
          Logout
        </button>
      </NavbarItem>
    </>
  ) : (
    <NavbarItem>
      <Link href="/sign-in">
        <button className="flex items-center justify-center text-base dark:bg-primary dark:text-black dark:hover:text-white transition text-primary px-4 py-3 border border-primary hover:cursor-pointer w-[85px] h-[45px] text-center">
          <span>Sign In</span>
        </button>
      </Link>
    </NavbarItem>
  );
}
