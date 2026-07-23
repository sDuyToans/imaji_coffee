import { ReactElement, ReactNode, useMemo, useState } from "react";
import toast from "react-hot-toast";
import {
  HiArrowPath,
  HiArrowTrendingDown,
  HiArrowTrendingUp,
  HiChatBubbleBottomCenterText,
  HiChevronRight,
  HiExclamationTriangle,
  HiFaceSmile,
  HiLightBulb,
  HiOutlineChatBubbleLeftRight,
  HiOutlineClipboardDocumentList,
  HiOutlineCube,
  HiOutlineMegaphone,
  HiOutlinePresentationChartLine,
  HiOutlineShieldExclamation,
  HiOutlineSparkles,
  HiPaperAirplane,
  HiStar,
} from "react-icons/hi2";

import DefaultLayout from "@/layouts/default.tsx";
import AccountHeader from "@/components/ui/account/account_header.tsx";
import {
  useAskAdminAiQuestionMutation,
  useGetAdminAiSuggestedQuestionsQuery,
  useGetAdminAiSummaryQuery,
} from "@/api/admin/adminAiApi.ts";

export default function AdminAiInsightsPage(): ReactElement {
  const {
    data: summary,
    isLoading,
    isError,
    refetch,
    isFetching,
  } = useGetAdminAiSummaryQuery();
  const { data: suggested } = useGetAdminAiSuggestedQuestionsQuery();
  const [askQuestion, { isLoading: isAsking }] =
    useAskAdminAiQuestionMutation();
  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState<string>("");
  const [evidence, setEvidence] = useState<string[]>([]);
  const [lastQuestion, setLastQuestion] = useState<string>("");
  const [chatError, setChatError] = useState<string>("");

  const hasSummaryData = useMemo(() => {
    if (!summary) return false;

    return (
      summary.metrics.length > 0 ||
      summary.popularProducts.length > 0 ||
      summary.lowStockProducts.length > 0 ||
      summary.topPromoCodes.length > 0 ||
      summary.riskAlerts.length > 0 ||
      summary.inventoryRecommendations.length > 0 ||
      summary.feedbackSummary.analyzedMessages > 0
    );
  }, [summary]);

  const confidenceScore = useMemo(() => {
    if (!summary) return 0;
    const checks = [
      summary.metrics.length > 0,
      summary.popularProducts.length > 0,
      summary.lowStockProducts.length > 0,
      summary.topPromoCodes.length > 0,
      summary.riskAlerts.length > 0,
      summary.inventoryRecommendations.length > 0,
      summary.feedbackSummary.analyzedMessages > 0,
    ];
    const available = checks.filter(Boolean).length;

    return Math.round((available / checks.length) * 100);
  }, [summary]);

  const executiveSummary = useMemo(() => {
    if (!summary) return "";
    const topProduct = summary.popularProducts[0];
    const lowStockCount = summary.lowStockProducts.length;
    const highRiskCount = summary.riskAlerts.filter(
      (a) => a.severity.toUpperCase() === "HIGH",
    ).length;
    const topPromo = summary.topPromoCodes[0];

    const parts = [
      topProduct
        ? `${topProduct.productName} is currently leading sales with ${topProduct.quantity} units sold.`
        : "No top-selling product was detected in this period.",
      lowStockCount > 0
        ? `${lowStockCount} products are in low-stock range and may need urgent restocking.`
        : "Inventory levels are currently stable with no low-stock alerts.",
      topPromo
        ? `${topPromo.code} is the most-used promo code so far (${topPromo.usageCount} uses).`
        : "No promo-code usage was detected for this window.",
      highRiskCount > 0
        ? `${highRiskCount} high-severity risk alert${highRiskCount > 1 ? "s are" : " is"} active and should be reviewed.`
        : "No high-severity risk alerts were detected.",
    ];

    return parts.join(" ");
  }, [summary]);

  const handleAsk = async (rawQuestion?: string) => {
    const q = (rawQuestion ?? question).trim();

    if (!q) return;
    setChatError("");
    try {
      const res = await askQuestion({ question: q }).unwrap();

      setLastQuestion(q);
      setAnswer(res.answer);
      setEvidence(res.evidence || []);
      setQuestion(q);
    } catch (error) {
      const message =
        (typeof error === "object" &&
          error !== null &&
          "data" in error &&
          typeof (error as { data?: { message?: string } }).data?.message ===
            "string" &&
          (error as { data: { message: string } }).data.message) ||
        "Unable to process AI question right now.";

      toast.error(message);
      setLastQuestion(q);
      setChatError(message);
      setAnswer(message);
      setEvidence([]);
    }
  };

  const handleRefresh = async () => {
    try {
      await refetch();
      toast.success("Insights refreshed");
    } catch {
      toast.error("Unable to refresh insights");
    }
  };

  return (
    <DefaultLayout>
      <AccountHeader
        content={
          "Premium admin intelligence for sales performance, inventory signals, customer feedback, and risk monitoring."
        }
        title={"AI Business Insights"}
      />

      <section className="bg-[#F8F3ED] px-5 py-10 lg:px-[124px] lg:py-14">
        {isLoading && <DashboardSkeleton />}
        {!isLoading && isError && (
          <ErrorPanel
            subtitle={"Please check your connection or try refreshing again."}
            title={"Unable to load AI insights"}
            onRetry={handleRefresh}
          />
        )}
        {!isLoading && !isError && summary && !hasSummaryData && (
          <EmptyPanel
            subtitle={
              "AI Insights will appear once there is enough order, promo, and customer activity."
            }
            title={"No analytics data yet"}
            onRetry={handleRefresh}
          />
        )}
        {!isLoading && !isError && summary && hasSummaryData && (
          <div className="flex flex-col gap-8 text-[#2F1E12]">
            <HeroHeader
              confidenceScore={confidenceScore}
              generatedAt={summary.generatedAt}
              isRefreshing={isFetching}
              onRefresh={handleRefresh}
            />

            <ExecutiveSummaryCard
              confidenceScore={confidenceScore}
              disclaimer={summary.disclaimer}
              text={executiveSummary}
            />

            <section
              aria-labelledby="overview-heading"
              className="space-y-4"
              id="overview"
            >
              <SectionHeader
                description={
                  "Live KPIs generated from backend sales and operations data."
                }
                icon={<HiOutlinePresentationChartLine className="h-5 w-5" />}
                id={"overview-heading"}
                title={"Overview"}
              />
              <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
                {summary.metrics.map((metric, index) => (
                  <MetricCard
                    key={metric.label}
                    animationDelay={index * 80}
                    metric={metric}
                  />
                ))}
              </div>
            </section>

            <section
              aria-labelledby="sales-performance-heading"
              className="space-y-4"
              id="sales-performance"
            >
              <SectionHeader
                description={
                  "Best sellers and movement patterns across your product catalog."
                }
                icon={<HiOutlineClipboardDocumentList className="h-5 w-5" />}
                id={"sales-performance-heading"}
                title={"Sales and product performance"}
              />
              <div className="grid gap-6 xl:grid-cols-2">
                <DataCard title={"Popular products"}>
                  <div className="flex flex-col gap-4">
                    <HorizontalBarChart
                      items={summary.popularProducts.map((p) => ({
                        label: p.productName,
                        value: p.quantity,
                      }))}
                      title={"Top products by units sold"}
                    />
                    <SimpleTable
                      headers={["Product", "Category", "Sold"]}
                      rows={summary.popularProducts.map((p) => [
                        p.productName,
                        p.category,
                        String(p.quantity),
                      ])}
                    />
                  </div>
                </DataCard>
                <DataCard title={"Inventory pressure map"}>
                  <div className="flex flex-col gap-4">
                    <HorizontalBarChart
                      inverse
                      items={summary.lowStockProducts.map((p) => ({
                        label: p.productName,
                        value: p.currentStock,
                      }))}
                      title={"Current stock by low-stock product"}
                    />
                    <LowStockList
                      rows={summary.lowStockProducts.map((p) => ({
                        name: p.productName,
                        category: p.category,
                        stock: p.currentStock,
                      }))}
                    />
                  </div>
                </DataCard>
              </div>
            </section>

            <section
              aria-labelledby="inventory-heading"
              className="space-y-4"
              id="inventory-intelligence"
            >
              <SectionHeader
                description={
                  "AI recommendations to proactively maintain stock health."
                }
                icon={<HiOutlineCube className="h-5 w-5" />}
                id={"inventory-heading"}
                title={"Inventory intelligence"}
              />
              <DataCard title={"Recommended actions"}>
                <div className="grid gap-4 md:grid-cols-2">
                  {summary.inventoryRecommendations.length === 0 ? (
                    <p className="text-sm text-[#7A6E62]">
                      No recommendations available.
                    </p>
                  ) : (
                    summary.inventoryRecommendations.map((rec, idx) => (
                      <RecommendationCard
                        key={`${rec.title}-${idx}`}
                        confidence={rec.confidence}
                        recommendation={rec.recommendation}
                        title={rec.title}
                      />
                    ))
                  )}
                </div>
              </DataCard>
            </section>

            <section
              aria-labelledby="promotions-heading"
              className="space-y-4"
              id="promotions"
            >
              <SectionHeader
                description={"Promo adoption trends and impact signals."}
                icon={<HiOutlineMegaphone className="h-5 w-5" />}
                id={"promotions-heading"}
                title={"Promotions"}
              />
              <DataCard title={"Promo-code usage"}>
                <div className="grid gap-5 lg:grid-cols-[1.1fr,1fr]">
                  <HorizontalBarChart
                    items={summary.topPromoCodes.map((p) => ({
                      label: p.code,
                      value: p.usageCount,
                    }))}
                    title={"Most-used promo codes"}
                  />
                  <SimpleTable
                    headers={["Code", "Uses"]}
                    rows={summary.topPromoCodes.map((p) => [
                      p.code,
                      String(p.usageCount),
                    ])}
                  />
                </div>
              </DataCard>
            </section>

            <section
              aria-labelledby="feedback-heading"
              className="space-y-4"
              id="customer-feedback"
            >
              <SectionHeader
                description={
                  "Customer sentiment snapshots and recurring support pain points."
                }
                icon={<HiChatBubbleBottomCenterText className="h-5 w-5" />}
                id={"feedback-heading"}
                title={"Customer feedback"}
              />
              <DataCard title={"Feedback analysis"}>
                <FeedbackSummaryPanel
                  analyzedMessages={summary.feedbackSummary.analyzedMessages}
                  note={summary.feedbackSummary.note}
                  recurringIssues={summary.feedbackSummary.recurringIssues}
                  sentiment={summary.feedbackSummary.sentiment}
                />
              </DataCard>
            </section>

            <section
              aria-labelledby="risk-heading"
              className="space-y-4"
              id="risk-alerts"
            >
              <SectionHeader
                description={
                  "Suspicious or unusual operational activity requiring admin review."
                }
                icon={<HiOutlineShieldExclamation className="h-5 w-5" />}
                id={"risk-heading"}
                title={"Risk alerts"}
              />
              <DataCard title={"Order and payment risk feed"}>
                <div className="grid gap-3">
                  {summary.riskAlerts.length === 0 ? (
                    <p className="text-sm text-[#7A6E62]">
                      No alerts detected.
                    </p>
                  ) : (
                    summary.riskAlerts.map((alert, idx) => (
                      <RiskAlertCard
                        key={`${alert.title}-${idx}`}
                        alert={alert}
                      />
                    ))
                  )}
                </div>
              </DataCard>
            </section>

            <section
              aria-labelledby="ask-ai-heading"
              className="space-y-4"
              id="ask-ai"
            >
              <SectionHeader
                description={
                  "Ask natural-language business questions and get recommendation-focused insights."
                }
                icon={<HiOutlineSparkles className="h-5 w-5" />}
                id={"ask-ai-heading"}
                title={"Ask the AI assistant"}
              />
              <AiAssistantPanel
                answer={answer}
                chatError={chatError}
                evidence={evidence}
                isAsking={isAsking}
                lastQuestion={lastQuestion}
                question={question}
                setQuestion={setQuestion}
                suggestedQuestions={suggested?.questions ?? []}
                onAsk={handleAsk}
              />
            </section>
          </div>
        )}
      </section>
    </DefaultLayout>
  );
}

function HeroHeader({
  generatedAt,
  onRefresh,
  isRefreshing,
  confidenceScore,
}: {
  generatedAt: string;
  onRefresh: () => void;
  isRefreshing: boolean;
  confidenceScore: number;
}): ReactElement {
  return (
    <header
      aria-live="polite"
      className="rounded-3xl border border-[#E6DACE] bg-gradient-to-r from-[#FFF8F1] via-[#FFF6ED] to-[#F8EEE4] p-6 shadow-sm lg:p-8"
    >
      <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div className="space-y-2">
          <p className="inline-flex items-center gap-2 rounded-full border border-[#EAD9C6] bg-white px-3 py-1 text-xs font-medium text-[#71533B]">
            <HiOutlineSparkles className="h-4 w-4 text-primary" />
            AI status: Active
          </p>
          <h2 className="text-3xl font-semibold text-[#2E1A0E] lg:text-4xl">
            AI Business Insights
          </h2>
          <p className="max-w-3xl text-sm text-[#6E5846] lg:text-base">
            A decision-focused view of sales, inventory, promotions, customer
            sentiment, and operational risk — generated from your live backend
            data.
          </p>
        </div>
        <div className="flex flex-col items-start gap-3 sm:flex-row sm:items-center">
          <div className="rounded-2xl border border-[#E9DCCA] bg-white px-4 py-2 text-sm text-[#5A4636]">
            <p className="font-medium">Confidence: {confidenceScore}%</p>
            <p className="text-xs text-[#8B7462]">
              Last updated: {new Date(generatedAt).toLocaleString()}
            </p>
          </div>
          <button
            aria-label="Refresh AI insights"
            className="inline-flex items-center gap-2 rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white transition hover:bg-primary-hover disabled:opacity-60 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
            disabled={isRefreshing}
            type="button"
            onClick={onRefresh}
          >
            <HiArrowPath
              className={`h-4 w-4 ${isRefreshing ? "animate-spin" : ""}`}
            />
            {isRefreshing ? "Refreshing..." : "Refresh"}
          </button>
        </div>
      </div>
    </header>
  );
}

function ExecutiveSummaryCard({
  text,
  disclaimer,
  confidenceScore,
}: {
  text: string;
  disclaimer: string;
  confidenceScore: number;
}): ReactElement {
  return (
    <article className="relative overflow-hidden rounded-3xl border border-[#E7D8C6] bg-gradient-to-br from-[#2F1D13] via-[#4A2D1D] to-[#7A4B31] p-6 text-[#FFF6ED] shadow-md lg:p-8">
      <div className="absolute -right-10 -top-10 h-32 w-32 rounded-full bg-white/10 blur-2xl" />
      <div className="relative flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div className="space-y-2">
          <p className="inline-flex items-center gap-2 rounded-full bg-white/10 px-3 py-1 text-xs font-medium text-[#FFE3C6]">
            <HiStar className="h-4 w-4" />
            AI Executive Summary
          </p>
          <p className="text-sm leading-6 lg:text-base">{text}</p>
        </div>
        <div className="min-w-[180px] rounded-2xl border border-white/20 bg-white/10 p-3 text-sm">
          <p className="font-semibold">Confidence indicator</p>
          <div className="mt-2 h-2 w-full overflow-hidden rounded-full bg-white/20">
            <div
              className="h-full rounded-full bg-[#FFD39C]"
              style={{ width: `${confidenceScore}%` }}
            />
          </div>
          <p className="mt-1 text-xs text-[#FFE9D2]">
            {confidenceScore}% data coverage
          </p>
        </div>
      </div>
      <p className="relative mt-4 text-xs text-[#F2DCC7]">{disclaimer}</p>
    </article>
  );
}

function SectionHeader({
  id,
  title,
  description,
  icon,
}: {
  id: string;
  title: string;
  description: string;
  icon: ReactNode;
}): ReactElement {
  return (
    <div className="flex items-start justify-between gap-4">
      <div>
        <h3
          className="flex items-center gap-2 text-xl font-semibold text-[#2E1A0E]"
          id={id}
        >
          <span className="inline-flex h-8 w-8 items-center justify-center rounded-lg bg-[#F0E2D3] text-primary">
            {icon}
          </span>
          {title}
        </h3>
        <p className="mt-1 text-sm text-[#6E5846]">{description}</p>
      </div>
      <button
        className="hidden items-center gap-1 rounded-lg border border-[#E5D7C7] bg-white px-3 py-2 text-xs font-medium text-[#6E5846] transition hover:border-primary hover:text-primary md:inline-flex"
        type="button"
      >
        View details
        <HiChevronRight className="h-3.5 w-3.5" />
      </button>
    </div>
  );
}

function DataCard({
  title,
  children,
}: {
  title: string;
  children: ReactNode;
}): ReactElement {
  return (
    <article className="rounded-3xl border border-[#E6D8CA] bg-white p-5 shadow-sm transition duration-300 hover:-translate-y-0.5 hover:shadow-md lg:p-6">
      <h4 className="mb-3 text-lg font-semibold text-[#2E1A0E]">{title}</h4>
      {children}
    </article>
  );
}

function MetricCard({
  metric,
  animationDelay,
}: {
  metric: { label: string; value: string; trend: string };
  animationDelay: number;
}): ReactElement {
  const trend = metric.trend.toLowerCase();
  const isPositive = trend.includes("up") || trend.includes("increase");
  const isNegative = trend.includes("down") || trend.includes("decrease");
  const trendColor = isPositive
    ? "text-green-hover"
    : isNegative
      ? "text-red-hover"
      : "text-blue-hover";
  const tint = isPositive
    ? "from-[#ECF9F4] to-[#F7FFFB]"
    : isNegative
      ? "from-[#FFF0EE] to-[#FFF8F7]"
      : "from-[#EEF6FF] to-[#F9FCFF]";
  const Icon = metricIcon(metric.label);

  return (
    <article
      className={`rounded-2xl border border-[#E8DACB] bg-gradient-to-br ${tint} p-4 shadow-sm transition duration-300 hover:-translate-y-0.5 hover:shadow-md`}
      style={{ animationDelay: `${animationDelay}ms` }}
    >
      <div className="flex items-start justify-between">
        <div className="inline-flex h-10 w-10 items-center justify-center rounded-xl bg-white text-primary shadow-sm">
          <Icon className="h-5 w-5" />
        </div>
        <span
          className={`inline-flex items-center gap-1 text-xs font-medium ${trendColor}`}
        >
          {isPositive ? (
            <HiArrowTrendingUp className="h-3.5 w-3.5" />
          ) : isNegative ? (
            <HiArrowTrendingDown className="h-3.5 w-3.5" />
          ) : (
            <HiChevronRight className="h-3.5 w-3.5 rotate-90" />
          )}
          {metric.trend}
        </span>
      </div>
      <p className="mt-3 text-xs font-medium text-[#7A6E62]">{metric.label}</p>
      <p className="mt-1 text-3xl font-semibold text-[#2E1A0E]">
        {metric.value}
      </p>
    </article>
  );
}

function metricIcon(label: string) {
  const key = label.toLowerCase();

  if (key.includes("revenue")) return HiOutlinePresentationChartLine;
  if (key.includes("refund")) return HiArrowTrendingDown;
  if (key.includes("orders")) return HiOutlineClipboardDocumentList;

  return HiLightBulb;
}

function SimpleTable({
  headers,
  rows,
}: {
  headers: string[];
  rows: string[][];
}): ReactElement {
  return (
    <div className="overflow-x-auto rounded-xl border border-[#EFE3D6]">
      <table className="min-w-full text-sm bg-white">
        <thead>
          <tr className="border-b border-[#EFE7DD] bg-[#FBF7F2]">
            {headers.map((header) => (
              <th
                key={header}
                className="px-2 py-2 text-left font-medium text-[#7A6E62]"
              >
                {header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={index} className="border-b border-[#F6EFE6] last:border-0">
              {row.map((cell, cellIndex) => (
                <td key={cellIndex} className="px-2 py-2 text-[#2E1A0E]">
                  {cell}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function HorizontalBarChart({
  title,
  items,
  inverse = false,
}: {
  title: string;
  items: { label: string; value: number }[];
  inverse?: boolean;
}): ReactElement {
  const max = Math.max(...items.map((item) => item.value), 1);

  return (
    <div
      aria-label={title}
      className="rounded-xl border border-[#EFE7DD] bg-[#FFFCF8] p-3"
      role="img"
    >
      <p className="mb-2 text-xs font-medium text-[#7A6E62]">{title}</p>
      <div className="flex flex-col gap-2">
        {items.map((item) => {
          const width = Math.max((item.value / max) * 100, 6);

          return (
            <div key={item.label} className="flex flex-col gap-1">
              <div className="flex items-center justify-between text-xs text-[#5B4A3B]">
                <span className="truncate pr-2">{item.label}</span>
                <span>{item.value}</span>
              </div>
              <div className="h-2 w-full overflow-hidden rounded-full bg-[#F3EADF]">
                <div
                  className={`h-full rounded-full transition-all duration-500 ${inverse ? "bg-[#D1753E]" : "bg-primary"}`}
                  style={{ width: `${width}%` }}
                />
              </div>
              <p className="text-right text-[10px] text-[#907A67]">
                {Math.round(width)}%
              </p>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function LowStockList({
  rows,
}: {
  rows: { name: string; category: string; stock: number }[];
}): ReactElement {
  if (rows.length === 0) {
    return <p className="text-sm text-[#7A6E62]">No low-stock products.</p>;
  }

  return (
    <div className="grid gap-2">
      {rows.map((row) => {
        const urgency =
          row.stock <= 3 ? "high" : row.stock <= 6 ? "medium" : "low";
        const urgencyClass =
          urgency === "high"
            ? "border-red-border bg-red-surface-light text-red-hover"
            : urgency === "medium"
              ? "border-yellow-border bg-yellow-surface-light text-yellow-hover"
              : "border-blue-border bg-blue-surface-light text-blue-hover";

        return (
          <div
            key={`${row.name}-${row.category}`}
            className="flex items-center justify-between rounded-xl border border-[#EFE3D6] bg-white px-3 py-2"
          >
            <div>
              <p className="text-sm font-medium text-[#2E1A0E]">{row.name}</p>
              <p className="text-xs text-[#8B7462]">{row.category}</p>
            </div>
            <span
              className={`rounded-full border px-2 py-1 text-xs font-semibold ${urgencyClass}`}
            >
              Stock {row.stock}
            </span>
          </div>
        );
      })}
    </div>
  );
}

function RiskAlertCard({
  alert,
}: {
  alert: { severity: string; title: string; description: string };
}): ReactElement {
  const severity = alert.severity.toUpperCase();
  const tone =
    severity === "HIGH"
      ? "border-red-border bg-red-surface-light"
      : severity === "MEDIUM"
        ? "border-yellow-border bg-yellow-surface-light"
        : "border-blue-border bg-blue-surface-light";
  const textTone =
    severity === "HIGH"
      ? "text-red-hover"
      : severity === "MEDIUM"
        ? "text-yellow-hover"
        : "text-blue-hover";

  return (
    <article className={`rounded-2xl border p-4 ${tone}`}>
      <div className="mb-1 flex items-center gap-2">
        <HiExclamationTriangle className={`h-4 w-4 ${textTone}`} />
        <p className={`text-xs font-semibold ${textTone}`}>{severity}</p>
      </div>
      <p className="text-sm font-semibold text-[#2E1A0E]">{alert.title}</p>
      <p className="mt-1 text-sm text-[#5B4A3B]">{alert.description}</p>
    </article>
  );
}

function RecommendationCard({
  title,
  recommendation,
  confidence,
}: {
  title: string;
  recommendation: string;
  confidence: string;
}): ReactElement {
  const confidenceTone =
    confidence.toUpperCase() === "HIGH"
      ? "bg-green-surface-light text-green-hover border-green-border"
      : confidence.toUpperCase() === "MEDIUM"
        ? "bg-yellow-surface-light text-yellow-hover border-yellow-border"
        : "bg-blue-surface-light text-blue-hover border-blue-border";

  return (
    <article className="rounded-2xl border border-[#E9DCCF] bg-[#FFFCF8] p-4">
      <div className="mb-2 flex items-start justify-between gap-3">
        <div className="inline-flex h-9 w-9 items-center justify-center rounded-lg bg-[#EFE2D5] text-primary">
          <HiLightBulb className="h-5 w-5" />
        </div>
        <span
          className={`rounded-full border px-2 py-1 text-[11px] font-semibold ${confidenceTone}`}
        >
          {confidence}
        </span>
      </div>
      <p className="text-sm font-semibold text-[#2E1A0E]">{title}</p>
      <p className="mt-1 text-sm text-[#5B4A3B]">{recommendation}</p>
    </article>
  );
}

function FeedbackSummaryPanel({
  sentiment,
  analyzedMessages,
  note,
  recurringIssues,
}: {
  sentiment: string;
  analyzedMessages: number;
  note: string;
  recurringIssues: string[];
}): ReactElement {
  const normalized = sentiment.toLowerCase();
  const tone = normalized.includes("negative")
    ? "bg-red-surface-light border-red-border text-red-hover"
    : normalized.includes("positive")
      ? "bg-green-surface-light border-green-border text-green-hover"
      : "bg-yellow-surface-light border-yellow-border text-yellow-hover";

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-3 rounded-2xl border border-[#E9DCCF] bg-[#FFFCF8] p-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="space-y-1">
          <p className="text-xs text-[#7A6E62]">Sentiment overview</p>
          <span
            className={`inline-flex items-center gap-1 rounded-full border px-3 py-1 text-xs font-semibold ${tone}`}
          >
            <HiFaceSmile className="h-4 w-4" />
            {sentiment}
          </span>
        </div>
        <div className="rounded-xl border border-[#E8DBCD] bg-white px-3 py-2 text-sm text-[#5B4A3B]">
          Messages analyzed: <strong>{analyzedMessages}</strong>
        </div>
      </div>

      <p className="text-sm text-[#6E5846]">{note}</p>

      <div className="grid gap-2 sm:grid-cols-2">
        {recurringIssues.length === 0 ? (
          <p className="text-sm text-[#7A6E62]">
            No recurring issues detected.
          </p>
        ) : (
          recurringIssues.map((issue) => (
            <div
              key={issue}
              className="rounded-xl border border-[#E8DBCD] bg-white px-3 py-2 text-sm text-[#4B392D]"
            >
              {issue}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

function AiAssistantPanel({
  question,
  setQuestion,
  onAsk,
  suggestedQuestions,
  answer,
  evidence,
  isAsking,
  lastQuestion,
  chatError,
}: {
  question: string;
  setQuestion: (value: string) => void;
  onAsk: (question?: string) => Promise<void> | void;
  suggestedQuestions: string[];
  answer: string;
  evidence: string[];
  isAsking: boolean;
  lastQuestion: string;
  chatError: string;
}): ReactElement {
  return (
    <DataCard title={"AI Assistant"}>
      <div className="space-y-4">
        <div className="flex items-start gap-3 rounded-2xl border border-[#E9DCCF] bg-[#FFF9F2] p-4">
          <div className="inline-flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary text-white">
            <HiOutlineSparkles className="h-5 w-5" />
          </div>
          <div>
            <p className="text-sm font-semibold text-[#2E1A0E]">
              Imaji AI Assistant
            </p>
            <p className="text-sm text-[#6E5846]">
              Ask about performance, inventory, promos, or risks.
              Recommendations are advisory and not guaranteed facts.
            </p>
          </div>
        </div>

        <div className="flex flex-wrap gap-2">
          {suggestedQuestions.length === 0 && (
            <p className="text-sm text-[#7A6E62]">
              Loading suggested questions...
            </p>
          )}
          {suggestedQuestions.map((q) => (
            <button
              key={q}
              aria-label={`Ask suggested question: ${q}`}
              className="rounded-full border border-[#D9CEC1] bg-white px-3 py-1 text-xs text-[#5B4A3B] transition hover:border-primary hover:text-primary focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
              type="button"
              onClick={() => onAsk(q)}
            >
              {q}
            </button>
          ))}
        </div>

        <div className="flex flex-col gap-2 sm:flex-row">
          <input
            aria-label="Ask AI analytics question"
            className="w-full rounded-xl border border-[#D9CEC1] bg-white px-4 py-3 text-sm outline-none transition focus:border-primary focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
            placeholder="Ask about sales, inventory, promotions, or risks..."
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                onAsk();
              }
            }}
          />
          <button
            aria-label="Send AI question"
            className="inline-flex items-center justify-center gap-2 rounded-xl bg-primary px-4 py-3 text-sm font-medium text-white transition hover:bg-primary-hover disabled:opacity-60 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
            disabled={isAsking}
            type="button"
            onClick={() => onAsk()}
          >
            {isAsking ? (
              <span className="inline-flex items-center gap-2">
                <span className="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent" />
                Thinking...
              </span>
            ) : (
              <>
                <HiPaperAirplane className="h-4 w-4" />
                Ask AI
              </>
            )}
          </button>
        </div>

        {!answer && !isAsking && (
          <div className="rounded-xl border border-dashed border-[#D8CCBF] bg-white px-4 py-6 text-center">
            <HiOutlineChatBubbleLeftRight className="mx-auto h-6 w-6 text-[#A27B5C]" />
            <p className="mt-2 text-sm font-medium text-[#5A4636]">
              Ask your first analytics question
            </p>
            <p className="text-xs text-[#8B7462]">
              Example: Which products may run out soon?
            </p>
          </div>
        )}

        {lastQuestion && (
          <div className="rounded-2xl border border-[#E8DBCD] bg-white px-4 py-3">
            <p className="text-xs font-medium text-[#8B7462]">You asked</p>
            <p className="text-sm text-[#2E1A0E]">{lastQuestion}</p>
          </div>
        )}

        {answer && (
          <div
            className={`rounded-2xl border px-4 py-4 ${chatError ? "border-red-border bg-red-surface-light" : "border-[#E7D9CB] bg-[#FFF8F0]"}`}
          >
            <p className="text-xs font-medium text-[#8B7462]">
              AI recommendation
            </p>
            <p className="mt-1 text-sm text-[#2E1A0E]">{answer}</p>
            {evidence.length > 0 && (
              <details className="mt-3 rounded-lg border border-[#E8DBCD] bg-white p-3">
                <summary className="cursor-pointer text-xs font-medium text-[#5A4636]">
                  Evidence and reasoning ({evidence.length})
                </summary>
                <ul className="mt-2 list-disc pl-5 text-xs text-[#5B4A3B]">
                  {evidence.map((item, idx) => (
                    <li key={`${item}-${idx}`}>{item}</li>
                  ))}
                </ul>
              </details>
            )}
          </div>
        )}
      </div>
    </DataCard>
  );
}

function DashboardSkeleton(): ReactElement {
  return (
    <div aria-busy="true" aria-live="polite" className="space-y-6">
      <div className="h-40 animate-pulse rounded-3xl bg-[#EFE3D7]" />
      <div className="h-32 animate-pulse rounded-3xl bg-[#EADDCF]" />
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
        {Array.from({ length: 5 }).map((_, index) => (
          <div
            key={index}
            className="h-32 animate-pulse rounded-2xl bg-[#F1E6DB]"
          />
        ))}
      </div>
      <div className="grid gap-6 xl:grid-cols-2">
        <div className="h-72 animate-pulse rounded-3xl bg-[#F4EADF]" />
        <div className="h-72 animate-pulse rounded-3xl bg-[#F4EADF]" />
      </div>
    </div>
  );
}

function ErrorPanel({
  title,
  subtitle,
  onRetry,
}: {
  title: string;
  subtitle: string;
  onRetry: () => void;
}): ReactElement {
  return (
    <div className="rounded-2xl border border-red-border bg-red-surface-light p-6 text-center">
      <HiExclamationTriangle className="mx-auto h-7 w-7 text-red-hover" />
      <p className="mt-2 text-lg font-semibold text-red-hover">{title}</p>
      <p className="mt-1 text-sm text-[#6E5846]">{subtitle}</p>
      <button
        className="mt-4 rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white transition hover:bg-primary-hover"
        type="button"
        onClick={onRetry}
      >
        Retry
      </button>
    </div>
  );
}

function EmptyPanel({
  title,
  subtitle,
  onRetry,
}: {
  title: string;
  subtitle: string;
  onRetry: () => void;
}): ReactElement {
  return (
    <div className="rounded-2xl border border-dashed border-[#CDB9A3] bg-[#FFF9F2] p-8 text-center">
      <HiOutlineSparkles className="mx-auto h-7 w-7 text-primary" />
      <p className="mt-2 text-lg font-semibold text-[#2E1A0E]">{title}</p>
      <p className="mt-1 text-sm text-[#6E5846]">{subtitle}</p>
      <button
        className="mt-4 rounded-xl border border-primary px-4 py-2 text-sm font-medium text-primary transition hover:bg-primary hover:text-white"
        type="button"
        onClick={onRetry}
      >
        Refresh
      </button>
    </div>
  );
}
