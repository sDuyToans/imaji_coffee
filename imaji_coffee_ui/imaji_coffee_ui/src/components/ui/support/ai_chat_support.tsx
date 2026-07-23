import { ReactElement, useMemo, useState } from "react";
import { IoArrowBack, IoSend } from "react-icons/io5";
import { HiOutlineSparkles } from "react-icons/hi2";

import {
  findBestSupportTopic,
  searchSupportTopics,
  type SupportTopic,
} from "@/components/ui/support/supportFaq.ts";

interface Message {
  id: string;
  text: string;
  sender: "user" | "assistant";
  timestamp: Date;
}

interface AIChatSupportProps {
  onBack: () => void;
}

const welcomeMessage =
  "Hi! I can answer common questions about orders, shipping, payments, and returns. Search a topic or ask in your own words.";

export default function AIChatSupport({
  onBack,
}: AIChatSupportProps): ReactElement {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "welcome",
      text: welcomeMessage,
      sender: "assistant",
      timestamp: new Date(),
    },
  ]);
  const [inputText, setInputText] = useState("");
  const [searchText, setSearchText] = useState("");

  const suggestions = useMemo(
    () => searchSupportTopics(searchText).slice(0, 6),
    [searchText],
  );

  const handleSendMessage = (text: string): void => {
    const content = text.trim();

    if (!content) {
      return;
    }

    const userMessage: Message = {
      id: `${Date.now()}-user`,
      text: content,
      sender: "user",
      timestamp: new Date(),
    };

    const topic = findBestSupportTopic(content);
    const assistantText = topic
      ? `${topic.question}\n\n${topic.answer}`
      : `I couldn't find an exact FAQ match for "${content}". Please use the search above, or contact the admin team for help with your specific order or account.`;

    const assistantMessage: Message = {
      id: `${Date.now()}-assistant`,
      text: assistantText,
      sender: "assistant",
      timestamp: new Date(),
    };

    setMessages((current) => [...current, userMessage, assistantMessage]);
    setInputText("");
  };

  const handleQuickPick = (topic: SupportTopic): void => {
    setSearchText(topic.question);
    handleSendMessage(topic.question);
  };

  const handleBack = (): void => {
    if (messages.length > 1) {
      setMessages([
        {
          id: "welcome",
          text: welcomeMessage,
          sender: "assistant",
          timestamp: new Date(),
        },
      ]);
      setInputText("");
      setSearchText("");

      return;
    }

    onBack();
  };

  return (
    <div className="flex h-[520px] flex-col rounded-2xl border border-gray-200 bg-white dark:border-gray-800 dark:bg-gray-950">
      <div className="border-b border-gray-200 p-4 dark:border-gray-800">
        <button
          className="mb-4 flex items-center gap-2 text-sm text-gray-600 transition hover:text-gray-900 dark:text-gray-400 dark:hover:text-white"
          type="button"
          onClick={handleBack}
        >
          <IoArrowBack className="h-4 w-4" />
          {messages.length > 1 ? "New conversation" : "Back to options"}
        </button>

        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10 text-primary">
            <HiOutlineSparkles className="h-5 w-5" />
          </div>
          <div>
            <p className="text-sm font-medium text-gray-900 dark:text-white">
              FAQ Assistant
            </p>
            <p className="text-xs text-gray-500 dark:text-gray-400">
              Fast answers from approved support topics
            </p>
          </div>
        </div>

        <div className="mt-4">
          <input
            className="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-primary dark:border-gray-700 dark:bg-gray-900 dark:text-white"
            placeholder="Search returns, shipping, payments..."
            type="text"
            value={searchText}
            onChange={(event) => setSearchText(event.target.value)}
          />
        </div>
      </div>

      <div className="flex-1 space-y-4 overflow-y-auto p-4">
        <div className="space-y-3">
          {messages.map((message) => (
            <div
              key={message.id}
              className={`flex ${message.sender === "user" ? "justify-end" : "justify-start"}`}
            >
              <div
                className={`max-w-[85%] rounded-2xl px-4 py-3 text-sm ${
                  message.sender === "user"
                    ? "bg-primary text-white"
                    : "bg-gray-100 text-gray-900 dark:bg-gray-900 dark:text-white"
                }`}
              >
                <p className="whitespace-pre-line">{message.text}</p>
                <span className="mt-1 block text-xs opacity-70">
                  {message.timestamp.toLocaleTimeString([], {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </span>
              </div>
            </div>
          ))}
        </div>

        <div className="rounded-2xl border border-gray-200 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900">
          <p className="text-xs font-medium uppercase tracking-wide text-gray-500 dark:text-gray-400">
            Suggested topics
          </p>
          <div className="mt-3 flex flex-wrap gap-2">
            {suggestions.length > 0 ? (
              suggestions.map((topic) => (
                <button
                  key={topic.id}
                  className="rounded-full border border-gray-300 bg-white px-3 py-1.5 text-xs text-gray-700 transition hover:border-primary hover:text-primary dark:border-gray-700 dark:bg-gray-950 dark:text-gray-300"
                  type="button"
                  onClick={() => handleQuickPick(topic)}
                >
                  {topic.question}
                </button>
              ))
            ) : (
              <p className="text-sm text-gray-600 dark:text-gray-400">
                No FAQ match found. Try another keyword or contact support.
              </p>
            )}
          </div>
        </div>
      </div>

      <div className="border-t border-gray-200 p-4 dark:border-gray-800">
        <div className="flex gap-2">
          <input
            className="flex-1 rounded-xl border border-gray-300 bg-white px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-primary dark:border-gray-700 dark:bg-gray-900 dark:text-white"
            placeholder="Ask a question..."
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                handleSendMessage(inputText);
              }
            }}
          />
          <button
            className="flex items-center justify-center rounded-xl bg-primary px-4 text-white transition hover:bg-primary-hover"
            type="button"
            onClick={() => handleSendMessage(inputText)}
          >
            <IoSend className="h-5 w-5" />
          </button>
        </div>
      </div>
    </div>
  );
}
