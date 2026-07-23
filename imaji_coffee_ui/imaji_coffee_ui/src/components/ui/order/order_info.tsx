import { ReactElement, ReactNode, useState } from "react";
import { CheckCircleIcon } from "@heroicons/react/24/solid";

import { formatDate } from "@/utils/formatDate.ts";
import DrawerUI from "@/components/layouts/drawer.tsx";
import PrimaryButton from "@/components/ui/button/primary_button.tsx";

function getPaymentMethodLabel(paymentMethod: string): string {
  switch (paymentMethod) {
    case "card":
      return "Credit or Debit Card";
    case "paypal":
      return "PayPal";
    case "cod":
      return "Cash on Delivery";
    default:
      return paymentMethod;
  }
}

export default function OrderInfo({
  transactionDate,
  paymentMethod,
  paymentStatus,
  shipMethod,
  total,
}: {
  transactionDate: string;
  paymentMethod: string;
  paymentStatus?: string | null;
  shipMethod: string;
  total: number;
}): ReactElement {
  const [isOpenTracking, setIsOpenTracking] = useState(false);
  const orderSteps = [
    {
      status: "SUCCESSFULLY DELIVERED",
      date: "Thursday, 4 April 2023, 08:17 AM",
      isDone: false,
    },
    {
      status: "PROCESSED AT DELIVERY CENTER",
      date: "Thursday, 4 April 2023, 03:07 AM",
      isDone: false,
    },
    {
      status: "ARRIVED AT SORTING CENTER",
      date: "Monday, 3 April 2023, 22:45 PM",
      isDone: false,
    },
    {
      status: "SHIPMENT EN ROUTE",
      date: "Monday, 3 April 2023, 22:34 PM",
      isDone: true,
    },
    {
      status: "ARRIVED AT SORTING CENTER",
      date: "Monday, 3 April 2023, 15:33 PM",
      isDone: true,
    },
    {
      status: "SHIPMENT EN ROUTE",
      date: "Monday, 3 April 2023, 12:14 PM",
      isDone: true,
    },
  ];

  return (
    <div
      className={
        "flex flex-col gap-6 lg:gap-5 pb-6 border-b border-neutral-200 w-full"
      }
    >
      <DrawerUI
        isOpen={isOpenTracking}
        onClose={() => setIsOpenTracking(false)}
      >
        <div
          className={
            "flex flex-col lg:flex-row items-center h-screen gap-6 lg:pl-[80px]"
          }
        >
          <div className={"flex-1 lg:flex-8/12"}>
            <h5 className={"text-6xl font-medium lg:text-8xl"}>Track Order</h5>
            <div>
              {orderSteps.map((step, index) => (
                <div
                  key={index}
                  className="flex items-start gap-4 mb-8 relative"
                >
                  <div className="z-10 mt-1">
                    <CheckCircleIcon
                      className={`${step.isDone ? "text-primary" : "text-dark-grey-70"} w-6 h-6 bg-white rounded-full`}
                    />
                  </div>
                  <div>
                    <p className="font-semibold">{step.status}</p>
                    <p className="text-sm text-gray-500">{step.date}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
          <div
            className={
              "flex-1 lg:flex-4/12 w-full bg-[#FCF7EF] px-5 py-6 lg:px-[48px] lg:pt-[80px] flex flex-col gap-[14px] lg:gap-[48px] h-full "
            }
          >
            <p className={"text-xl lg:text-4xl dark:text-primary"}>
              Order Summary
            </p>
            <OrderInfoItems
              isShowTotal={true}
              paymentMethod={paymentMethod}
              paymentStatus={paymentStatus}
              shipMethod={shipMethod}
              total={total}
              transactionDate={transactionDate}
            />
          </div>
        </div>
      </DrawerUI>
      <OrderInfoItems
        paymentMethod={paymentMethod}
        paymentStatus={paymentStatus}
        shipMethod={shipMethod}
        total={total}
        transactionDate={transactionDate}
      >
        <PrimaryButton
          content={"Tracking"}
          type={"button"}
          onPress={() => setIsOpenTracking(true)}
        />
      </OrderInfoItems>
    </div>
  );
}

function OrderInfoItems({
  transactionDate,
  paymentMethod,
  paymentStatus,
  shipMethod,
  total,
  isShowTotal = false,
  children,
}: {
  transactionDate: string;
  paymentMethod: string;
  paymentStatus?: string | null;
  shipMethod: string;
  total: number;
  isShowTrackingBtn?: boolean;
  isShowTotal?: boolean;
  children?: ReactNode;
}) {
  const paymentDate = formatDate(transactionDate);

  return (
    <>
      <div
        className={
          "flex flex-row text-base lg:text-xl gap-2 lg:flex-col justify-between lg:gap-5"
        }
      >
        <p className={"font-medium text-dark-grey-70 dark:text-primary"}>
          Transaction Date
        </p>
        <p className={"font-normal dark:text-black"}>{paymentDate}</p>
      </div>
      <div
        className={
          "flex flex-row text-base lg:text-xl gap-2 lg:flex-col justify-between lg:gap-5"
        }
      >
        <p className={"font-medium  text-dark-grey-70 dark:text-primary"}>
          Payment Method
        </p>
        <p className={"font-normal dark:text-black"}>
          {getPaymentMethodLabel(paymentMethod)}
        </p>
      </div>
      {paymentStatus && (
        <div
          className={
            "flex flex-row text-base lg:text-xl gap-2 lg:flex-col justify-between lg:gap-5"
          }
        >
          <p className={"font-medium text-dark-grey-70 dark:text-primary"}>
            Payment Status
          </p>
          <p className={"font-normal dark:text-black"}>
            {paymentStatus.charAt(0) + paymentStatus.slice(1).toLowerCase()}
          </p>
        </div>
      )}
      <div className={"flex flex-col gap-4"}>
        <div
          className={
            "flex flex-row text-base lg:text-xl gap-2 lg:flex-col justify-between lg:gap-5"
          }
        >
          <p className={"font-medium text-dark-grey-70 dark:text-primary"}>
            Ship Method
          </p>
          <div
            className={
              " flex flex-col lg:flex-row gap-3 items-start lg:items-center dark:text-black"
            }
          >
            <p className={"font-normal"}>{shipMethod}</p>
          </div>
        </div>
        {children}
      </div>
      {isShowTotal && total && (
        <div className={"flex flex-col gap-1"}>
          <h5
            className={
              "font-medium text-base lg:text-lg text-dark-grey-70 dark:text-primary"
            }
          >
            Order Total
          </h5>
          <p className={"text-xl lg:text-3xl font-medium dark:text-black"}>
            ${total}
          </p>
        </div>
      )}
    </>
  );
}
