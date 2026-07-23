import { ReactElement } from "react";
import { Card, CardBody, CardFooter } from "@heroui/card";
import { Image } from "@heroui/image";
import { useNavigate } from "react-router-dom";

import { ProductItem } from "@/types";

export default function ProductItemCard({
  product,
}: {
  product: ProductItem;
}): ReactElement {
  const { name, price, images, oldPrice, isAvailableAtWeb, quantity } = product;
  const safeImages = images ?? [];
  const heroImgUrl =
    safeImages.find((img) => img.isMain)?.imageUrl ?? safeImages[0]?.imageUrl;
  const navigate = useNavigate();

  function getDiscountPercentage(product: ProductItem): number {
    if (product.oldPrice <= 0 || product.oldPrice <= product.price) {
      return 0;
    }

    return Math.round(
      ((product.oldPrice - product.price) / product.oldPrice) * 100,
    );
  }
  let salePercentage: number = getDiscountPercentage(product);
  let classBorderHover: string =
    "relative inline-block text-white text-2xl\n" +
    "         before:content-[''] before:absolute before:left-0 before:top-[-5px] before:w-full before:h-[2px] before:bg-gradient-to-r before:from-gray-500 before:to-[#A27B5C]-300 before:scale-x-0 before:origin-left before:transition-transform before:duration-400 before:ease-out\n" +
    "         after:content-[''] after:absolute after:left-0 after:bottom-[-5px] after:w-full after:h-[2px] after:bg-gradient-to-r after:from-gray-500 after:to-[#A27B5C]-300 after:scale-x-0 after:origin-right after:transition-transform after:duration-400 after:ease-out\n" +
    "         hover:before:scale-x-100 hover:after:scale-x-100 hover:shadow-xl";

  return (
    <div className={classBorderHover}>
      <Card
        className={"rounded-none shadow-primary dark:shadow-white w-full"}
        isPressable={true}
        shadow={"none"}
        onPress={() => navigate(`/menu/detail/${product.productId}`)}
      >
        <CardBody className={"overflow-visible p-0 relative"}>
          {heroImgUrl ? (
            <Image
              alt={name}
              classNames={{
                wrapper: "w-full h-[160px] lg:h-[300px]",
                img: "w-full h-full object-cover rounded-none",
              }}
              radius={"lg"}
              shadow={"none"}
              src={heroImgUrl}
              width={"100%"}
            />
          ) : (
            <div className="w-full h-[160px] lg:h-[300px] bg-gray-100 dark:bg-gray-800 flex items-center justify-center">
              <span className="text-sm text-gray-500 dark:text-gray-400">
                No image available
              </span>
            </div>
          )}
          {quantity <= 0 && (
            <div
              className={
                "uppercase bg-black text-white w-[192px] h-[52px]  flex items-center justify-center absolute top-0 left-0 z-10"
              }
            >
              <span>out of stock</span>
            </div>
          )}
          {salePercentage > 0 && quantity > 0 && (
            <div
              className={
                "uppercase bg-[#F14C35] text-white w-[59px] h-[36px] lg:w-[91px] lg:h-[52px] flex items-center justify-center absolute top-0 left-0 z-10"
              }
            >
              <span>sale</span>
            </div>
          )}
        </CardBody>
        <CardFooter
          className={
            "text-small justify-between items-start flex flex-col gap-3"
          }
        >
          <div className={"flex flex-col gap-1 items-start"}>
            <p className={"text-base font-medium uppercase text-left"}>
              {name}
            </p>
            <div className={"text-xs text-[#7F7F7F] text-left"}>
              {isAvailableAtWeb ? (
                <span>Available Buy at Website</span>
              ) : (
                <span>Only at Cafe and Delivery</span>
              )}
            </div>
          </div>
          <div className={"flex flex-col items-start gap-1"}>
            <div className={"flex gap-1 items-center"}>
              <p className={"text-xl"}>{price}.00$</p>
              <p className={"text-sm line-through"}>{oldPrice}.00$</p>
            </div>
            <p className={"text-primary text-xs text-start"}>
              Get 20% Off in the App
            </p>
          </div>
        </CardFooter>
      </Card>
    </div>
  );
}
