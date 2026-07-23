import React, { ReactElement, useCallback } from "react";
import useEmblaCarousel from "embla-carousel-react";
import "./embla.css";
import AutoScroll from "embla-carousel-auto-scroll";
import { EmblaOptionsType } from "embla-carousel";

import {
  NextButton,
  PrevButton,
  usePrevNextButtons,
} from "@/libs/slider/prev_next_button_carousel.tsx";
import { SliderItem } from "@/types";
import SliderItemCard from "@/components/ui/sliders/slider_item_card.tsx";

type PropsType = {
  slides: SliderItem[];
  options?: EmblaOptionsType;
  isLoop: boolean;
  title?: string;
  pos?: "top" | "bottom";
  isShowControl: boolean;
};

export default function CustomCarousel(props: PropsType): React.ReactElement {
  let { slides, options, isLoop, title, pos, isShowControl } = props;

  options = { ...options, loop: isLoop };
  const [emblaRef, emblaApi] = useEmblaCarousel(options, [
    AutoScroll({
      playOnInit: true,
      speed: 1, // try values: 2 → slow, 4 → medium, 6 → fast
      startDelay: 0, // no delay before scrolling starts
      stopOnInteraction: false, // don’t stop if user clicks next/prev
      stopOnMouseEnter: false, // don’t pause on hover
    }),
  ]);
  const {
    prevBtnDisabled,
    nextBtnDisabled,
    onPrevButtonClick,
    onNextButtonClick,
  } = usePrevNextButtons(emblaApi);
  const onButtonAutoplayClick = useCallback(
    (callback: () => void) => {
      const autoScroll = emblaApi?.plugins()?.autoScroll;

      if (!autoScroll) return;

      const resetOrStop =
        autoScroll.options.stopOnInteraction === false
          ? autoScroll.reset
          : autoScroll.stop;

      resetOrStop();
      callback();
    },
    [emblaApi],
  );

  return (
    <div className="embla">
      <div
        className={
          `${title ? " flex justify-between" : "flex justify-end"}` +
          " items-center"
        }
      >
        <p className={"font-semibold md:text-2xl lg:text-3xl"}>{title}</p>
      </div>
      <div ref={emblaRef} className="embla__viewport">
        <div className="embla__container">
          {slides.map(
            (s: SliderItem, index: number): ReactElement => (
              <div key={index} className="embla__slide">
                <div className="embla__slide__number">
                  <SliderItemCard descPos={pos} item={s} />
                </div>
              </div>
            ),
          )}
        </div>
      </div>
      {isShowControl && (
        <div className="embla__controls">
          <div className="embla__buttons">
            <PrevButton
              disabled={prevBtnDisabled}
              onClick={() => onButtonAutoplayClick(onPrevButtonClick)}
            />
            <NextButton
              disabled={nextBtnDisabled}
              onClick={() => onButtonAutoplayClick(onNextButtonClick)}
            />
          </div>
        </div>
      )}
    </div>
  );
}
