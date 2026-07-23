import { ReactElement } from "react";
import { Spinner } from "@heroui/spinner";

import { EventItem } from "@/types";
import SliderItemCard from "@/components/ui/sliders/slider_item_card.tsx";
import { useGetClosedEventQuery } from "@/api/events/eventsApi.ts";

export default function ClosedEvent(): ReactElement {
  return (
    <div className="flex flex-col gap-8 lg:gap-12">
      <div className="px-5 lg:px-[124px]">
        <h2 className="text-4xl lg:text-6xl font-medium">Past Events</h2>
        <div className="mt-4 w-20 h-1 bg-primary dark:bg-white" />
        <p className="mt-4 text-gray-600 dark:text-gray-400">
          Explore the memorable moments from our previous gatherings
        </p>
      </div>
      <EventList />
    </div>
  );
}

function EventList(): ReactElement {
  const { data: closedEvents, isLoading } = useGetClosedEventQuery();

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-20">
        <Spinner color="primary" size="lg" />
      </div>
    );
  }

  if (!closedEvents || closedEvents.length === 0) {
    return (
      <div className="px-5 lg:px-[124px] py-12 text-center">
        <p className="text-gray-500 dark:text-gray-400">
          No past events to display yet.
        </p>
      </div>
    );
  }

  return (
    <div className="px-5 lg:px-[124px] grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 auto-rows-fr gap-6 lg:gap-8">
      {closedEvents.map((ev: EventItem, index: number): ReactElement => {
        const isFeature = index === 0;
        const delay = index * 0.1;

        return (
          <div
            key={ev.eventId}
            className={`group ${isFeature ? "lg:col-span-2 lg:row-span-2" : "lg:row-span-1"}`}
            style={{
              animation: `fadeInUp 0.6s ease-out ${delay}s both`,
            }}
          >
            <SliderItemCard descPos="bottom" item={ev} styles="h-full" />
          </div>
        );
      })}
    </div>
  );
}
