import { useEffect, useState } from "react";
import LightGallery from "lightgallery/react";
import lgZoom from "lightgallery/plugins/zoom";
import lgVideo from "lightgallery/plugins/video";
// @ts-ignore
import fjGallery from "flickr-justified-gallery";

import "lightgallery/css/lightgallery.css";
import "lightgallery/css/lg-zoom.css";
import "lightgallery/css/lg-thumbnail.css";

import DefaultLayout from "@/layouts/default.tsx";
import PageHeading from "@/components/ui/page_heading.tsx";
import { useGetSpacesQuery } from "@/api/spaces/spacesApi.ts";

export default function Space() {
  const { data: spaces } = useGetSpacesQuery();
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    setIsVisible(true);
  }, []);

  useEffect(() => {
    if (!spaces?.length) return;

    // Delay gallery initialization to ensure animations don't conflict
    const timer = setTimeout(() => {
      fjGallery(document.querySelectorAll(".gallery"), {
        itemSelector: ".gallery__item",
        rowHeight: 320,
        gutter: 24,
        lastRow: "justify",
      });
    }, 100);

    return () => clearTimeout(timer);
  }, [spaces]);

  return (
    <DefaultLayout>
      <div className="py-[48px] lg:py-[80px] flex flex-col gap-[48px]">
        <div
          className={`px-5 lg:px-[124px] transition-all duration-1000 ${
            isVisible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-8"
          }`}
        >
          <PageHeading
            className="text-left md:text-center"
            title="Our Spaces"
          />
          <p className="text-base lg:text-xl text-center max-w-3xl mx-auto mt-6 text-gray-700 dark:text-gray-300">
            We provide many attractive and unique workspaces so you will have no
            trouble finding the workspace you want. Click any image to view full
            size.
          </p>
        </div>

        <main
          className={`px-5 lg:px-[124px] transition-all duration-1000 delay-200 ${
            isVisible ? "opacity-100 translate-y-0" : "opacity-0 translate-y-8"
          }`}
        >
          <LightGallery
            elementClassNames="gallery"
            mode="lg-fade"
            plugins={[lgZoom, lgVideo]}
            thumbnail={true}
            zoom={true}
          >
            {spaces &&
              spaces.map((s) => (
                <a
                  key={s.id}
                  className="gallery__item"
                  data-lg-size="1200-900"
                  href={s.image}
                >
                  <img
                    alt={s.name || "space"}
                    className="hover:opacity-90 transition-opacity cursor-pointer"
                    src={s.image}
                  />
                </a>
              ))}
          </LightGallery>
        </main>
      </div>

      <style>{`
        @keyframes fadeInUp {
          from {
            opacity: 0;
            transform: translateY(30px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
      `}</style>
    </DefaultLayout>
  );
}
