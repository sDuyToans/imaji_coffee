import { useEffect, ReactElement, useState } from "react";
import { Image } from "@heroui/image";
import { GoDotFill } from "react-icons/go";

import PageHeading from "@/components/ui/page_heading.tsx";

/**
 * @author duytoan
 * @since 05/2026
 */
export default function HomeNew(): ReactElement {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    setIsVisible(true);
  }, []);

  return (
    <div className="px-5 lg:px-[124px] flex flex-col gap-[48px] lg:gap-[56px] py-[48px] lg:py-[80px]">
      {/* Title Section with animation */}
      <div
        className={`transition-all duration-1000 ${
          isVisible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-12"
        }`}
      >
        <NewTitle />
      </div>

      {/* Content Section with delayed animation */}
      <div
        className={`transition-all duration-1000 delay-200 w-full ${
          isVisible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-12"
        }`}
      >
        <NewContent />
      </div>
    </div>
  );
}

/**
 * @author duytoan
 * @since 05/2026
 */
function NewTitle(): ReactElement {
  return (
    <div className="flex flex-col gap-6 items-start">
      <div>
        <PageHeading className="text-left md:text-center" title="Latest News" />
        <div className="mt-4 w-20 h-1 bg-primary dark:bg-white md:mx-auto" />
      </div>
      <p className="text-base lg:text-xl text-center w-full text-gray-700 dark:text-gray-300 max-w-3xl mx-auto">
        Get the latest updates and deeper coffee experience from IMAJI Coffee
      </p>
    </div>
  );
}

/**
 * @author duytoan
 * @since 05/2026
 */
function NewContent(): ReactElement {
  return (
    <div className="flex flex-col lg:flex-row gap-8 lg:gap-12 items-center">
      <div className="flex-1 overflow-hidden group">
        <Image
          alt="featured news"
          className="w-full h-[320px] lg:h-[400px] object-cover hover:opacity-90 transition-opacity duration-300"
          src="/home/new/Sections/Image.png"
        />
      </div>
      <div className="flex-1 flex flex-col gap-4 lg:gap-5">
        <h3 className="text-3xl lg:text-4xl font-medium leading-tight text-gray-900 dark:text-white">
          Collaboration to Develop Coffee and Beverage Industry Expertise in
          Indonesia
        </h3>
        <div className="flex gap-3 items-center text-sm lg:text-base text-gray-600 dark:text-gray-400">
          <span>4 Min</span>
          <GoDotFill className="text-xs" />
          <span>August 19, 2022</span>
        </div>
        <p className="text-gray-600 dark:text-gray-400 leading-relaxed">
          Discover how we're working together to elevate Indonesia's coffee
          industry through innovation and expertise sharing.
        </p>
      </div>
    </div>
  );
}
