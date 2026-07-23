import { ReactElement, useEffect } from "react";
import { useParams } from "react-router-dom";
import { Spinner } from "@heroui/spinner";

import DefaultLayout from "@/layouts/default.tsx";
import { useGetNewByIdQuery, useGetNewsQuery } from "@/api/news/newsApi.ts";
import { NewItem } from "@/types";
import { formatDate } from "@/utils/formatDate.ts";
import { useScrollAnimation } from "@/hooks/useScrollAnimation.ts";

export default function NewDetail(): ReactElement {
  const { newId } = useParams<{ newId: string }>();
  const titleSection = useScrollAnimation({ threshold: 0.1 });
  const imageSection = useScrollAnimation({ threshold: 0.1 });
  const descSection = useScrollAnimation({ threshold: 0.1 });

  // Scroll to top when page loads
  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "instant" });
  }, [newId]);

  // find in cache first
  const { data: cachedNews } = useGetNewsQuery(undefined, {
    selectFromResult: ({ data }) => ({
      data: data?.find((n) => n.newId === Number(newId)),
    }),
  });

  // Fallback fetch if not cached
  const { data: newDetail, isLoading } = useGetNewByIdQuery(newId!, {
    skip: !!cachedNews,
  });

  const finalNew: NewItem | undefined = cachedNews || newDetail;

  if (isLoading) {
    return <DefaultLayout>{<Spinner color={"primary"} />}</DefaultLayout>;
  }

  return (
    <DefaultLayout>
      {finalNew && (
        <div
          className={
            "flex flex-col gap-[48px] lg:gap-[56px] px-5 lg:px-[124px] py-[48px] lg:py-[80px]"
          }
        >
          <div
            ref={titleSection.ref}
            className={`transition-all duration-1000 ${
              titleSection.isVisible
                ? "opacity-100 translate-y-0"
                : "opacity-0 translate-y-12"
            }`}
          >
            <NewTitle createdAt={finalNew.createdAt} title={finalNew.title} />
          </div>

          <div
            ref={imageSection.ref}
            className={`transition-all duration-1000 delay-200 ${
              imageSection.isVisible
                ? "opacity-100 scale-100"
                : "opacity-0 scale-95"
            }`}
          >
            <img
              alt={finalNew.title}
              className={
                "rounded-none w-full object-cover h-[400px] lg:h-[600px]"
              }
              src={finalNew.image}
            />
          </div>

          <div
            ref={descSection.ref}
            className={`transition-all duration-1000 delay-300 ${
              descSection.isVisible
                ? "opacity-100 translate-y-0"
                : "opacity-0 translate-y-12"
            }`}
          >
            <NewDescription desc={finalNew.description} />
          </div>
        </div>
      )}
    </DefaultLayout>
  );
}

function NewTitle({
  title,
  createdAt,
}: {
  title: string;
  createdAt: string;
}): ReactElement {
  const date: string = formatDate(createdAt);

  return (
    <div className={"flex flex-col gap-6 lg:gap-[30px]"}>
      <h1 className={"text-4xl lg:text-7xl text-center"}>{title}</h1>
      <p className={"text-center text-xl lg:text-3xl text-dark-grey-70"}>
        {date}
      </p>
    </div>
  );
}

function NewDescription({ desc }: { desc: string }): ReactElement {
  return <p className={"text-base lg:text-xl whitespace-pre-line"}>{desc}</p>;
}
