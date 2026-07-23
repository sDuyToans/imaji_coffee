import { ReactElement, useMemo, useState } from "react";
import { Spinner } from "@heroui/spinner";
import { Link } from "@heroui/link";
import { Accordion, AccordionItem } from "@heroui/accordion";
import toast from "react-hot-toast";

import {
  useCancelOrderMutation,
  useGetAccountOrdersQuery,
} from "@/api/account/accountApi.ts";
import { AccountOrderResponseDto } from "@/types";
import { formatDate } from "@/utils/formatDate.ts";
import Modal from "@/components/layouts/modal.tsx";
import PrimaryButton from "@/components/ui/button/primary_button.tsx";

const cancellableStatuses = new Set(["PENDING", "PAID", "PROCESSING"]);

function renderStatus(status: string): ReactElement {
  switch (status) {
    case "PENDING":
      return (
        <p
          className={
            "flex-1 text-center py-2 lg:py-3 px-1 bg-[#E3E3E3] text-xs lg:text-base h-[32px] w-[180px] flex justify-center items-center"
          }
        >
          Waiting For Payment
        </p>
      );
    case "PAID":
      return (
        <p
          className={
            "flex-1 text-center py-2 lg:py-3 px-1 bg-[#FFA90B] text-xs lg:text-base h-[32px] w-[180px] flex justify-center items-center"
          }
        >
          Paid
        </p>
      );
    case "PROCESSING":
      return (
        <p
          className={
            "flex-1 text-center py-2 lg:py-3 px-1 bg-[#FFA90B] text-xs lg:text-base h-[32px] w-[180px] flex justify-center items-center"
          }
        >
          Processed
        </p>
      );
    case "CANCELLED":
      return (
        <p
          className={
            "flex-1 text-center py-2 lg:py-3 px-1 bg-[#F14C35] text-xs lg:text-base h-[32px] w-[180px] flex justify-center items-center"
          }
        >
          Canceled
        </p>
      );
    case "SHIPPED":
      return (
        <p
          className={
            "flex-1 text-center py-2 lg:py-3 px-1 bg-[#129BFF] text-xs lg:text-base h-[32px] w-[180px] flex justify-center items-center"
          }
        >
          Shipped
        </p>
      );
    case "DELIVERED":
      return (
        <p
          className={
            "flex-1 text-center py-2 lg:py-3 px-1 bg-[#22D7A6] text-xs lg:text-base h-[32px] w-[180px] flex justify-center items-center"
          }
        >
          Delivered
        </p>
      );
    case "REFUNDED":
      return (
        <p
          className={
            "flex-1 text-center py-2 lg:py-3 px-1 bg-dark-grey-70 text-xs lg:text-base h-[32px] w-[180px] flex justify-center items-center"
          }
        >
          Refunded
        </p>
      );
    default:
      return (
        <p
          className={
            "flex-1 text-center py-3 px-1 bg-dark-grey-70 text-xs lg:text-base h-[32px] w-[180px] flex justify-center items-center"
          }
        >
          {status}
        </p>
      );
  }
}

function formatPaymentMethod(method?: string | null): string {
  switch (method?.toLowerCase()) {
    case "card":
      return "Credit or Debit Card";
    case "paypal":
      return "PayPal";
    case "cod":
      return "Cash on Delivery";
    default:
      return method ?? "-";
  }
}

export default function OrderItemStatus({
  search,
  status,
}: {
  search: string;
  status: string;
}): ReactElement {
  const { data, isLoading, isError, refetch } = useGetAccountOrdersQuery(
    status === "All" ? undefined : { status },
  );
  const [cancelOrder, { isLoading: isCancelling }] = useCancelOrderMutation();
  const [cancellingId, setCancellingId] = useState<number | null>(null);

  const filteredOrders = useMemo(() => {
    const list: AccountOrderResponseDto[] = data ?? [];
    const term = search.trim().toLowerCase();

    if (!term) return list;

    return list.filter((o) => String(o.orderId).toLowerCase().includes(term));
  }, [data, search]);

  if (isLoading) return <Spinner color={"primary"} />;

  if (isError) {
    return (
      <div className={"flex flex-col gap-4 items-center"}>
        <p>Failed to load orders.</p>
        <PrimaryButton type={"button"} onPress={() => refetch()}>
          Retry
        </PrimaryButton>
      </div>
    );
  }

  if (filteredOrders.length === 0) {
    return (
      <div className={"border border-[#E3E3E3] p-6 lg:p-8 text-center"}>
        <p className={"text-lg lg:text-xl"}>
          {search || status !== "All"
            ? "No orders match your filters."
            : "You have no orders. Please make one and come back."}
        </p>
      </div>
    );
  }

  const handleCancel = async () => {
    if (cancellingId == null) return;
    try {
      await cancelOrder(cancellingId).unwrap();
      toast.success("Order cancelled");
      setCancellingId(null);
    } catch (error) {
      const message =
        typeof error === "object" && error !== null && "data" in error
          ? (error as { data?: { message?: string } }).data?.message
          : undefined;

      toast.error(message ?? "Unable to cancel order. Please try again.");
    }
  };

  return (
    <>
      <div className={"hidden md:flex flex-col gap-5"}>
        <div className="flex gap-[48px] text-xl border-b border-neutral-400 pb-6">
          <div className="flex-4/12 text-center">Status</div>
          <div className="flex-2/12 text-center">Date</div>
          <div className="flex-2/12 text-center">OrderId</div>
          <div className="flex-2/12 text-center">Items</div>
          <div className="flex-2/12 text-center">Amount</div>
        </div>
        {filteredOrders.map(
          (o: AccountOrderResponseDto): ReactElement => (
            <div
              key={o.orderId}
              className={"flex gap-5 border-b border-[#E3E3E3] pb-5"}
            >
              <div className={"flex-4/12 text-center flex items-center"}>
                {renderStatus(o.status)}
              </div>
              <p className={"flex-2/12 text-center text-lg lg:text-xl"}>
                {formatDate(o.createdAt)}
              </p>
              <p
                className={
                  "text-primary flex-2/12 text-center text-lg lg:text-xl"
                }
              >
                <Link href={`/completed-checkout/${o.orderId}`}>
                  #{o.orderId}
                </Link>
              </p>
              <p className={"flex-2/12 text-center text-lg lg:text-xl"}>
                {o.items}
              </p>
              <p className={"flex-2/12 text-center text-lg lg:text-xl"}>
                ${o.amount}
              </p>
              <div className={"flex-2/12 text-center flex flex-col gap-2"}>
                <p className={"text-sm text-dark-grey-70"}>
                  {formatPaymentMethod(o.paymentMethod)}
                  {o.paymentStatus ? ` — ${o.paymentStatus}` : ""}
                </p>
                {cancellableStatuses.has(o.status) && (
                  <PrimaryButton
                    className={
                      "bg-transparent border-red-500 text-red-500 w-full h-9"
                    }
                    content={isCancelling ? "..." : "Cancel"}
                    disabled={isCancelling}
                    type={"button"}
                    onPress={() => setCancellingId(o.orderId)}
                  />
                )}
              </div>
            </div>
          ),
        )}
      </div>

      <div className={"flex flex-col gap-6 md:hidden"}>
        <div className="flex md:hidden gap-[48px] text-xl border-b border-neutral-400 pb-6 ">
          <div className="flex-1 text-center">Status</div>
          <div className="flex-1 text-center">Date</div>
        </div>
        <div className={"flex gap-5 border-b border-[#E3E3E3] pb-5"}>
          <Accordion>
            {filteredOrders.map((o, i) => (
              <AccordionItem
                key={o.orderId}
                aria-label={`Order number ${i + 1}`}
                title={`Order # ${i + 1}`}
              >
                <div className={"flex flex-col gap-4"}>
                  <div className={"flex justify-between items-center"}>
                    <div>{renderStatus(o.status)}</div>
                    <div>{formatDate(o.createdAt)}</div>
                  </div>
                  <div className={"flex gap-2"}>
                    <p
                      className={
                        "flex-1 text-center text-lg lg:text-xl flex flex-col gap-1 items-center"
                      }
                    >
                      <span>Order Id</span>
                      <Link href={`/completed-checkout/${o.orderId}`}>
                        <span className={"text-primary"}>#{o.orderId}</span>
                      </Link>
                    </p>
                    <p
                      className={
                        "flex-1 text-center text-lg lg:text-xl flex flex-col gap-1 items-center"
                      }
                    >
                      <span>Items</span>
                      <span>{o.items}</span>
                    </p>
                    <p
                      className={
                        "flex-1 text-center text-lg lg:text-xl flex flex-col gap-1 items-center"
                      }
                    >
                      <span>Amount</span>
                      <span>${o.amount}</span>
                    </p>
                  </div>
                  <p className={"text-center text-sm text-dark-grey-70"}>
                    {formatPaymentMethod(o.paymentMethod)}
                    {o.paymentStatus ? ` — ${o.paymentStatus}` : ""}
                  </p>
                  {cancellableStatuses.has(o.status) && (
                    <PrimaryButton
                      className={
                        "bg-transparent border-red-500 text-red-500 w-full h-9"
                      }
                      content={isCancelling ? "..." : "Cancel"}
                      disabled={isCancelling}
                      type={"button"}
                      onPress={() => setCancellingId(o.orderId)}
                    />
                  )}
                </div>
              </AccordionItem>
            ))}
          </Accordion>
        </div>
      </div>

      <Modal
        cancelText="Keep order"
        confirmText="Cancel order"
        isOpen={cancellingId != null}
        onClose={() => setCancellingId(null)}
        onConfirm={handleCancel}
      >
        <p className="text-lg py-4">
          Are you sure you want to cancel order #{cancellingId}?
        </p>
      </Modal>
    </>
  );
}
