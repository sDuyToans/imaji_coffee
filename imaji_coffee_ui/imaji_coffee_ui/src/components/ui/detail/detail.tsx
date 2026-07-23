import { ReactElement, useEffect, useState } from "react";
import { BreadcrumbItem, Breadcrumbs } from "@heroui/breadcrumbs";
import { FaCircle } from "react-icons/fa6";

import { imageItem, ProductItem } from "@/types";
import ProductDescription from "@/components/ui/product/product_description.tsx";
import ProductButtonsFunction from "@/components/ui/product/product_buttons_function.tsx";

/**
 * Detail component to display the detail of a product
 * @author duytoan
 * @since Mar 05 2026
 * @param product
 * @constructor
 */
export default function Detail({
  product,
}: Readonly<{
  product: ProductItem;
}>): ReactElement {
  const { name, price, oldPrice, description, images } = product;
  const [heroImg, setHeroImg] = useState(images[0].imageUrl);

  useEffect(() => {
    for (let img of images) {
      if (img.isMain) {
        setHeroImg(img.imageUrl);
      }
    }
  }, [product]);

  return (
    <div
      className={"flex flex-col lg:flex-row gap-[48px] py-[48px] lg:py-[80px]"}
    >
      <div className={"flex flex-col lg:flex-row gap-4 lg:gap-[48px]"}>
        <div className={"lg:order-2"}>
          <img
            alt={name}
            className={
              "rounded-none w-[350x] h-[400px] md:w-full md:h-[600px] lg:w-[468px] lg:h-[707.89px]"
            }
            src={heroImg}
          />
        </div>
        <div
          className={
            "w-4/5 mx-auto lg:w-auto flex gap-4 lg:flex-col justify-between lg:order-1"
          }
        >
          {/*sort image, let the isMain go first before render*/}
          {[...images]
            .sort((a, b) => {
              if (a.isMain && !b.isMain) return -1;
              if (!a.isMain && b.isMain) return 1;

              return a.id - b.id;
            })
            .map(
              (n: imageItem): ReactElement => (
                <button key={n.imageUrl} onClick={() => setHeroImg(n.imageUrl)}>
                  <img
                    alt={"alt"}
                    className={
                      "w-[52px] h-[52px] md:w-[104px] md:h-[104px] lg:w-[166.9px] lg:h-[164.97px] cursor-pointer hover:shadow-sm hover:shadow-primary dark:hover:shadow-white"
                    }
                    src={n.imageUrl}
                  />
                </button>
              ),
            )}
        </div>
      </div>
      <div
        className={"flex flex-col gap-[28px] lg:gap-[32px] w-full lg:w-[471px]"}
      >
        <div>
          <Breadcrumbs>
            <BreadcrumbItem href={"/"}>
              <span className={"text-base lg:text-xl"}>Home</span>
            </BreadcrumbItem>
            <BreadcrumbItem href={"/menu"}>
              {" "}
              <span className={"text-base lg:text-xl"}>Menu</span>
            </BreadcrumbItem>
            <BreadcrumbItem>
              {" "}
              <span className={"text-base lg:text-xl"}>Detail</span>
            </BreadcrumbItem>
          </Breadcrumbs>
        </div>
        <div className={"flex flex-col gap-4"}>
          <h3 className={"capitalize text-4xl lg:text-5xl"}>{name}</h3>
          <div className={"flex items-center gap-3"}>
            <span className={"text-2xl lg:text-4xl"}>{price}.00 $</span>
            {oldPrice > price && (
              <span className={"text-base lg:text-xl line-through"}>
                {oldPrice}.00 $
              </span>
            )}
            <FaCircle className={"text-primary"} size={8} />
            <span className={"text-primary"}>Get 20% Off in App</span>
          </div>
        </div>
        <ProductDescription description={description} />
        <ProductButtonsFunction product={product} />
      </div>
    </div>
  );
}
