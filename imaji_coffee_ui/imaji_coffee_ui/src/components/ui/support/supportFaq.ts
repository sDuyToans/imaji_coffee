export type SupportTopic = {
  id: string;
  category: "Orders" | "Shipping" | "Returns" | "Payments" | "Account";
  question: string;
  answer: string;
  keywords: string[];
};

export const supportTopics: SupportTopic[] = [
  {
    id: "return-policy",
    category: "Returns",
    question: "What is your return policy?",
    answer:
      "You can return unused items in their original packaging within 30 days of purchase. Refunds are processed after the item is received and checked.",
    keywords: ["return", "refund", "policy", "exchange"],
  },
  {
    id: "start-return",
    category: "Returns",
    question: "How do I return a product?",
    answer:
      "Open your order history, choose the item, and request a return. After approval, you can use the return instructions shown in your account.",
    keywords: ["how to return", "send back", "return product"],
  },
  {
    id: "shipping-time",
    category: "Shipping",
    question: "How long does shipping take?",
    answer:
      "Standard shipping usually takes 3-5 business days. Express shipping, when available, typically arrives in 1-2 business days.",
    keywords: ["shipping", "delivery", "arrive", "how long"],
  },
  {
    id: "track-order",
    category: "Orders",
    question: "How do I track my order?",
    answer:
      "Go to your order history and open the order details. The tracking number and shipment status will be shown there once the order is shipped.",
    keywords: ["track", "tracking", "order status", "where is my order"],
  },
  {
    id: "cancel-order",
    category: "Orders",
    question: "Can I cancel my order?",
    answer:
      "If the order has not been processed yet, you may be able to cancel it from your order details. If it is already being prepared or shipped, please contact support.",
    keywords: ["cancel", "stop order", "change order"],
  },
  {
    id: "payment-methods",
    category: "Payments",
    question: "What payment methods do you accept?",
    answer:
      "Available payment methods depend on checkout options shown on the order page. If a payment fails, try again with another method or contact support.",
    keywords: ["payment", "card", "checkout", "paypal", "stripe"],
  },
];

export function searchSupportTopics(query: string): SupportTopic[] {
  const normalized = query.trim().toLowerCase();

  if (!normalized) {
    return supportTopics;
  }

  return supportTopics.filter((topic) => {
    return [
      topic.question,
      topic.answer,
      topic.category,
      ...topic.keywords,
    ].some((field) => field.toLowerCase().includes(normalized));
  });
}

export function findBestSupportTopic(query: string): SupportTopic | null {
  const normalized = query.trim().toLowerCase();

  if (!normalized) {
    return null;
  }

  const byQuestion = supportTopics.find(
    (topic) =>
      normalized.includes(topic.question.toLowerCase()) ||
      topic.question.toLowerCase().includes(normalized),
  );

  if (byQuestion) {
    return byQuestion;
  }

  const byKeyword = supportTopics.find((topic) =>
    topic.keywords.some((keyword) =>
      normalized.includes(keyword.toLowerCase()),
    ),
  );

  return byKeyword ?? null;
}
