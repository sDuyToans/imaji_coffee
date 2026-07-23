import { ReactElement } from "react";
import { Spinner } from "@heroui/spinner";

import PageHeading from "@/components/ui/page_heading.tsx";
import { EventItem } from "@/types";
import SliderItemCard from "@/components/ui/sliders/slider_item_card.tsx";
import { useGetUpComingEventQuery } from "@/api/events/eventsApi.ts";

export default function UpComingEvent(): ReactElement {
  return (
    <div className={"flex flex-col gap-[48px] lg:gap-[56px]"}>
      <EventTitle />
      <EventList />
    </div>
  );
}

function EventTitle(): ReactElement {
  return (
    <div className="px-5 lg:px-[124px] flex flex-col lg:flex-row gap-6 lg:gap-[48px] items-start">
      <div className="lg:flex-1">
        <PageHeading className="text-left" title="Upcoming Events" />
        <div className="mt-4 w-20 h-1 bg-primary dark:bg-white" />
      </div>
      <div className="lg:flex-1 space-y-4">
        <p className="text-base lg:text-xl text-gray-700 dark:text-gray-300">
          We believe that we are big not because of us but because of them. They
          are the ones who motivate us to continue to innovate to provide a
          quality coffee taste and comfortable space that is getting better
          every day.
        </p>
        <p className="text-sm text-gray-500 dark:text-gray-400">
          Join us for unique experiences, workshops, and gatherings.
        </p>
      </div>
    </div>
  );
}

function EventList(): ReactElement {
  const { data: upcomingEvents, isLoading } = useGetUpComingEventQuery();

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <Spinner color="primary" size="lg" />
      </div>
    );
  }

  if (!upcomingEvents || upcomingEvents.length === 0) {
    return (
      <div className="px-5 lg:px-[124px] py-12 text-center">
        <p className="text-gray-500 dark:text-gray-400">
          No upcoming events at the moment. Check back soon!
        </p>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-6 lg:gap-8 md:flex-row px-5 lg:px-[124px]">
      {upcomingEvents.map(
        (event: EventItem, index: number): ReactElement => (
          <div
            key={event.eventId}
            className="flex-1 group"
            style={{
              animation: `fadeInUp 0.6s ease-out ${index * 0.15}s both`,
            }}
          >
            <SliderItemCard descPos="bottom" item={event} />
          </div>
        ),
      )}
    </div>
  );
}
