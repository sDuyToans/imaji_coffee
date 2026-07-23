import { ReactElement } from "react";
import { Link } from "@heroui/link";
import {
  NavbarBrand,
  NavbarContent,
  NavbarItem,
  NavbarMenuToggle,
  NavbarMenu,
  NavbarMenuItem,
  Navbar as HeroUINavBar,
} from "@heroui/navbar";
import { HiOutlineMenuAlt3 } from "react-icons/hi";
import { NavLink, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";

import { siteConfig } from "@/config/site.ts";
import { ThemeSwitch } from "@/components/theme-switch.tsx";
import Logo from "@/components/ui/logo.tsx";
import { navItem } from "@/types";
import { Icon } from "@/components/layouts/icons.tsx";
import DrawerUI from "@/components/layouts/drawer.tsx";
import Cart from "@/pages/cart/cart.tsx";
import { useCart } from "@/context/cart.tsx";
import { clearUser } from "@/features/auth/authSlice.ts";
import { cartApiBE, useGetCartQuery } from "@/api/cart/cartApi.ts";
import { useGetMeQuery } from "@/api/account/accountApi.ts";
import { useLogoutMutation } from "@/api/auth/authApi.ts";
import { apiSlice } from "@/api/jwt/apiSlice.ts";
import AuthButtons from "@/components/layouts/auth-buttons.tsx";

/**
 * @author duytoan
 * @since 08/2025
 */
export function Navbar(): ReactElement {
  const { data: cart } = useGetCartQuery();
  const { data: myInfo } = useGetMeQuery();

  let cartCount: number =
    cart?.cartItems?.reduce((sum, item) => sum + item.quantity, 0) ?? 0;
  const { isOpenCart, setIsOpenCart } = useCart();
  const [logoutApi] = useLogoutMutation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const username = myInfo?.username;
  const isLoggedIn = !!username;
  const isAdmin = myInfo?.roles?.includes("ROLE_ADMIN") ?? false;
  const navItems: navItem[] = isAdmin
    ? [
        ...siteConfig.navItems,
        { id: 999, label: "AI Insights", href: "/admin/ai-insights" },
      ]
    : siteConfig.navItems;

  const handleLogout = async () => {
    try {
      await logoutApi().unwrap();
    } catch {
      // Continue with logout even if API call fails
    }

    dispatch(clearUser());

    dispatch(apiSlice.util.resetApiState());
    dispatch(cartApiBE.util.resetApiState());

    navigate("/sign-in", { replace: true });
  };

  return (
    <HeroUINavBar
      className="sm:py-[20px] sm:px-[20px] lg:py-[32px] lg:px-[100px] dark:bg-[#FCF7EF]"
      maxWidth="xl"
      position="sticky"
    >
      {/* Left Side Logo */}
      <NavbarContent className="sm:basis-full" justify="start">
        <NavbarBrand className="gap-3 max-w-fit">
          <Link color="foreground" href="/">
            <Logo />
          </Link>
        </NavbarBrand>
      </NavbarContent>

      {/* Desktop Menu */}
      <NavbarContent
        className="hidden sm:flex basis-1/5 sm:basis-full"
        justify="end"
      >
        <div className="hidden lg:flex gap-2 items-center">
          {navItems.map((item: navItem) => (
            <NavbarItem key={item.label}>
              <NavLink
                className={({ isActive }) =>
                  isActive ? "active dark:text-black" : "dark:text-black"
                }
                color="foreground"
                to={item.href}
              >
                {item.label}
              </NavLink>
            </NavbarItem>
          ))}

          {/* Order Button */}
          <NavbarItem>
            <button onClick={() => setIsOpenCart(!isOpenCart)}>
              <div className="flex justify-center items-center text-base dark:bg-[#FCF7EF] dark:border-black dark:border dark:hover:text-secondary dark:text-black bg-primary text-white py-3 px-4 hover:cursor-pointer w-[85px] h-[45px] transition">
                <span>Order</span>
                {cartCount > 0 && <span> ({cartCount})</span>}
              </div>
            </button>
          </NavbarItem>

          {/* Auth Buttons */}
          <AuthButtons
            isLoggedIn={isLoggedIn}
            username={username || ""}
            onLogout={handleLogout}
          />

          <ThemeSwitch />
        </div>
      </NavbarContent>

      {/* Mobile Menu Toggle */}
      <NavbarContent className="lg:hidden basis-1 pl-4" justify="end">
        <div className="flex gap-3 items-center">
          <DrawerUI
            isOpen={isOpenCart}
            title="Cart"
            onClose={() => setIsOpenCart(false)}
          >
            <Cart onClose={() => setIsOpenCart(false)} />
          </DrawerUI>

          <button
            className="relative"
            onClick={() => setIsOpenCart(!isOpenCart)}
          >
            <Icon className="cursor-pointer" name="shopping-basket" />
            {cartCount > 0 && (
              <div className="w-6 h-6 bg-yellow rounded-full text-center absolute -top-3 -right-3 text-black">
                <span className="text-xs">{cartCount}</span>
              </div>
            )}
          </button>

          <NavbarMenuToggle
            className="text-black cursor-pointer"
            icon={<HiOutlineMenuAlt3 />}
          />
          <ThemeSwitch />
        </div>
      </NavbarContent>

      {/* Mobile Dropdown Menu */}
      <NavbarMenu className="bg-[#FCF7EF] w-full flex flex-col gap-8 pt-[120px] md:pt-[180px]">
        <div className="mx-4 mt-2 flex flex-col gap-8">
          {navItems.map((item: navItem) => (
            <NavbarMenuItem
              key={item.label}
              className="flex items-center justify-center"
            >
              <NavLink
                className={({ isActive }) =>
                  isActive
                    ? "active text-2xl text-black"
                    : "text-2xl text-black"
                }
                color="foreground"
                to={item.href}
              >
                {item.label}
              </NavLink>
            </NavbarMenuItem>
          ))}
        </div>

        {/* Auth Buttons Mobile */}
        <div className="flex flex-col justify-center items-center gap-8">
          <AuthButtons
            isLoggedIn={isLoggedIn}
            username={username || ""}
            onLogout={handleLogout}
          />
        </div>
      </NavbarMenu>
    </HeroUINavBar>
  );
}
