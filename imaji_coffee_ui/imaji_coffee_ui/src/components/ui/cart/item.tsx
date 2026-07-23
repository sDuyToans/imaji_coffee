import { ReactElement } from "react";
import { Image } from "@heroui/image";
import { IoIosClose } from "react-icons/io";

import { CartItemResponseDto } from "@/types";
import {
  useRemoveFromCartMutation,
  useUpdateCartMutation,
} from "@/api/cart/cartApi.ts";

export default function Item({
  item,
}: {
  item: CartItemResponseDto;
}): ReactElement {
  const [updateCart] = useUpdateCartMutation();
  const [removeFromCart] = useRemoveFromCartMutation();

  const increase = () => {
    updateCart({ cartItemId: item.cartItemId, quantity: item.quantity + 1 });
  };

  const decrease = () => {
    updateCart({ cartItemId: item.cartItemId, quantity: item.quantity - 1 });
  };

  const remove = () => {
    removeFromCart(item.cartItemId);
  };

  return (
    <div className="flex flex-col md:flex-row gap-[30px] items-center border-b border-neutral-300 py-6 ">
      {/* Product */}
      <div className="flex flex-3/5 gap-4 items-center">
        <div className={"w-1/3 md:w-1/2 flex items-start"}>
          <Image
            alt={"hero img"}
            className="rounded-none w-full h-[107.5px] md:h-[91.95px] md:w-[120px] object-cover"
            src={item.imageUrl}
          />
        </div>
        <div className="flex flex-col w-2/3">
          <p className="text-lg lg:text-xl">{item.productName.toUpperCase()}</p>
          <p className="text-sm text-[#7F7F7F]">
            IMAJI COFFEE {item.productCategory.toUpperCase()}
          </p>
          <div className="block md:hidden flex-1/5 text-lg">
            ${item.price}.00
          </div>
          <div className={"flex items-center gap-4 justify-between md:hidden"}>
            <div className="flex-1/5">
              <div className="flex items-center justify-between border border-black w-[80px] dark:border-primary">
                <button
                  className="px-2 py-1 text-lg cursor-pointer"
                  disabled={item.quantity === 1}
                  onClick={decrease}
                >
                  −
                </button>
                <span className="px-2 py-1">{item.quantity}</span>
                <button
                  className="px-2 py-1 text-lg cursor-pointer"
                  onClick={increase}
                >
                  +
                </button>
              </div>
            </div>
            <div className="flex-1/5 text-lg">
              ${(item.price * item.quantity).toFixed(2)}
            </div>
            <div className="w-[30px] text-center">
              <button className="text-black cursor-pointer" onClick={remove}>
                <IoIosClose className={"dark:text-primary"} size={28} />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Price */}
      <div className="hidden md:block flex-1/5 text-lg">${item.price}.00</div>

      {/* Quantity */}
      <div className="hidden md:block flex-1/5">
        <div className="flex items-center justify-between border border-black w-[120px] dark:border-primary">
          <button
            className="px-3 py-2 text-lg cursor-pointer"
            disabled={item.quantity === 1}
            onClick={decrease}
          >
            −
          </button>
          <span className="px-4 py-2">{item.quantity}</span>
          <button
            className="px-3 py-2 text-lg cursor-pointer"
            onClick={increase}
          >
            +
          </button>
        </div>
      </div>

      {/* Subtotal */}
      <div className="hidden md:block flex-1/5 text-lg">
        ${(item.price * item.quantity).toFixed(2)}
      </div>

      {/* Delete */}
      <div className="hidden md:block w-[50px] text-center">
        <button className="text-black cursor-pointer" onClick={remove}>
          <IoIosClose className={"dark:text-primary"} size={28} />
        </button>
      </div>
    </div>
  );
}
