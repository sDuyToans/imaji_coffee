import { ReactElement } from "react";
import { Image } from "@heroui/image";

import { ProductOrderDetail } from "@/types";

export default function YourOrder({
  items,
  total,
  tax,
  ship,
  discount,
}: {
  items: ProductOrderDetail[];
  total: number;
  tax: number;
  ship: number;
  discount: number;
}): ReactElement {
  return (
    <div className={"flex flex-col gap-6"}>
      <h2 className={"text-4xl text-left"}>Your Order</h2>
      <div className={"flex flex-col gap-4"}>
        {items &&
          items.map((i: ProductOrderDetail) => (
            <div
              key={i.productId}
              className={
                "pb-4 border-b border-gray-200 flex justify-between items-center"
              }
            >
              <div className={"flex items-center gap-4  w-full"}>
                <Image
                  alt={i.productName}
                  classNames={{
                    wrapper: "w-full h-full",
                    img: "w-[85px] h-[84px] rounded-none",
                  }}
                  src={i.productImg}
                />
                <div className={"flex flex-col gap-4 w-full"}>
                  <div className={"flex flex-col gap-1"}>
                    <p className={"text-base font-medium"}>
                      {i.productName.toUpperCase()}
                    </p>
                    <p>{i.productCategory.toUpperCase()}</p>
                  </div>
                  <div className={"flex justify-between"}>
                    <p>
                      {i.quantity} x ${i.price},00
                    </p>
                    <div className={"lg:hidden"}>
                      ${(i.quantity * i.price).toFixed(2)}
                    </div>
                  </div>
                </div>
              </div>
              <div className={"hidden lg:block"}>
                ${(i.quantity * i.price).toFixed(2)}
              </div>
            </div>
          ))}
      </div>
      <div className={"flex flex-col gap-4"}>
        <div className={"flex justify-between items-center"}>
          <p className={"text-xl lg:text-2xl font-medium"}>Subtotal</p>
          <p className={"text-xl lg:text-2xl font-medium"}>
            ${total - ship - tax}
          </p>
        </div>
        <div className={"flex justify-between items-center"}>
          <p className={"text-base lg:text-xl font-light"}>Tax (10%)</p>
          <p className={"text-base lg:text-xl font-light"}>${tax}</p>
        </div>
        <div className={"flex justify-between items-center"}>
          <p className={"text-base lg:text-xl font-light"}>Discount</p>
          <p className={"text-base lg:text-xl font-light"}>${discount}</p>
        </div>
        <div className={"flex justify-between items-center"}>
          <p className={"text-base lg:text-xl font-light"}>Shipping</p>
          <p className={"text-base lg:text-xl font-light"}>${ship}</p>
        </div>
        <div className={"flex justify-between items-center"}>
          <p className={"text-3xl lg:text-4xl font-medium"}>Grand Total</p>
          <p className={"text-3xl lg:text-4xl font-medium"}>${total}</p>
        </div>
      </div>
    </div>
  );
}
