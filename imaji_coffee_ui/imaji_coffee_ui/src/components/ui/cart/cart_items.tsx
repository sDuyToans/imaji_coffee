import { ReactElement } from "react";

import Item from "@/components/ui/cart/item.tsx";
import { CartItemResponseDto } from "@/types";

export default function CartItems({
  items,
}: {
  items: CartItemResponseDto[];
}): ReactElement {
  return (
    <div className="flex flex-col">
      {/* Header */}
      <div className="hidden lg:flex gap-[48px] text-xl border-b border-neutral-400 pb-6">
        <div className="flex-3/5">Product</div>
        <div className="flex-1/5">Price</div>
        <div className="flex-1/5">Quantity</div>
        <div className="flex-1/5">Subtotal</div>
        <div className="w-[50px] text-center">Delete</div>
      </div>

      {/* Items */}
      <div
        className={`
         overflow-y-auto max-h-[420px] md:max-h-[500px] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden  
        `}
      >
        {items && items.map((i) => <Item key={i.productId} item={i} />)}
      </div>
    </div>
  );
}
