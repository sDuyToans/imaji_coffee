import { ReactElement, useState } from "react";

export default function ProductDescription({
  description,
}: {
  description: string;
}): ReactElement {
  const [expanded, setExpaned] = useState(false);

  const previewLength = 200;
  const isLong: boolean = description.length > previewLength;

  const displayedText = expanded
    ? description
    : description.slice(0, previewLength) + (isLong ? "..." : "");

  return (
    <div className={"flex flex-col gap-2"}>
      <h6 className={"text-base lg:text-lg font-semibold"}>Description:</h6>
      <p className={"whitespace-pre-line text-base lg:text-lg"}>
        {displayedText}
      </p>
      {isLong && (
        <button
          className={"text-primary text-base text-left cursor-pointer"}
          onClick={() => setExpaned((prev) => !prev)}
        >
          {expanded ? "Read less" : "Read more"}
        </button>
      )}
    </div>
  );
}
