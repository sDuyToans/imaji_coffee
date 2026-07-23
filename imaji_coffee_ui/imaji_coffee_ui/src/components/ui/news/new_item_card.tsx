import { ReactElement } from "react";
import { Image } from "@heroui/image";
import { GoDotFill } from "react-icons/go";
import { Link } from "react-router-dom";

import { NewItem } from "@/types";
import { formatDate } from "@/utils/formatDate.ts";

interface Props {
  readonly newItem: NewItem;
}

/**
 * @author duytoan
 * @since 05/2026
 */
export default function NewItemCard({
  newItem,
}: Readonly<Props>): ReactElement {
  const { newId, title, image, createdAt, time } = newItem;

  const created_date: string = formatDate(createdAt);

  return (
    <Link
      className="group flex flex-col md:flex-row gap-6 lg:gap-8 py-8 lg:py-10 transition-all duration-300 hover:bg-gray-50 dark:hover:bg-gray-900 px-4"
      to={`/news/${newId}`}
    >
      {/* Image Container */}
      <div className="flex-shrink-0 w-full md:w-[360px] lg:w-[420px] h-[240px] md:h-[220px] lg:h-[240px] overflow-hidden">
        <Image
          alt={title}
          classNames={{
            wrapper: "w-full h-full",
            img: "w-full h-full object-cover group-hover:opacity-90 transition-opacity duration-300",
          }}
          src={image}
        />
      </div>

      {/* Content Container */}
      <div className="flex flex-col gap-3 lg:gap-4 flex-1 justify-center">
        {/* Title */}
        <h3 className="text-2xl md:text-3xl lg:text-4xl font-medium leading-tight text-gray-900 dark:text-white">
          {title}
        </h3>

        {/* Meta Information */}
        <div className="flex gap-3 items-center text-sm lg:text-base text-gray-600 dark:text-gray-400">
          <span>{time}</span>
          <GoDotFill className="text-xs" />
          <span>{created_date}</span>
        </div>
      </div>
    </Link>
  );
}
