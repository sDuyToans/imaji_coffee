import { ReactElement, useState } from "react";
import { HiOutlineChatBubbleLeftRight, HiOutlineUser } from "react-icons/hi2";
import { HiSparkles } from "react-icons/hi2";
import { useNavigate } from "react-router-dom";

import Modal from "@/components/layouts/modal.tsx";
import AIChatSupport from "@/components/ui/support/ai_chat_support.tsx";

export default function SupportWidget(): ReactElement {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState<"admin" | "ai" | null>(
    null,
  );
  const navigate = useNavigate();

  const handleClose = () => {
    setIsOpen(false);
    setSelectedOption(null);
  };

  const handleSelectAdmin = () => {
    setSelectedOption("admin");
    setIsOpen(false);
    navigate("/chat");
  };

  const handleSelectAI = () => {
    setSelectedOption("ai");
  };

  return (
    <>
      {/* Floating Support Button - Modern Chat Bubble */}
      <button
        style={{
          position: "fixed",
          bottom: "32px",
          right: "32px",
          width: "64px",
          height: "64px",
          backgroundColor: "#A27B5C",
          color: "white",
          borderRadius: "50% 50% 50% 8px",
          border: "none",
          cursor: "pointer",
          boxShadow: "0 10px 30px rgba(0,0,0,0.3)",
          zIndex: 9999,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
        onClick={() => setIsOpen(true)}
      >
        <HiSparkles style={{ width: "32px", height: "32px" }} />
        <div
          style={{
            position: "absolute",
            top: "-4px",
            right: "-4px",
            width: "16px",
            height: "16px",
            backgroundColor: "#22c55e",
            borderRadius: "50%",
            border: "2px solid white",
          }}
        />
      </button>

      {/* Support Modal */}
      <Modal
        cancelText=""
        confirmText=""
        haveFooter={false}
        isOpen={isOpen}
        styles="p-0 max-w-2xl"
        onClose={handleClose}
      >
        <div className="p-6 lg:p-8">
          {/* Header - Remove manual close button to avoid double X */}
          <div className="mb-6">
            <div className="flex items-center gap-3 mb-2">
              <div className="w-10 h-10 bg-gradient-to-br from-primary to-primary/80 rounded-full flex items-center justify-center">
                <HiSparkles className="w-6 h-6 text-white" />
              </div>
              <h2 className="text-2xl lg:text-3xl font-medium text-gray-900 dark:text-white">
                Customer Support
              </h2>
            </div>
            <p className="text-sm lg:text-base text-gray-600 dark:text-gray-400">
              How can we help you today?
            </p>
          </div>

          {/* Options or AI Chat */}
          {selectedOption === "ai" ? (
            <div className="space-y-4">
              <AIChatSupport onBack={() => setSelectedOption(null)} />
              <div className="flex items-center justify-between rounded-2xl border border-gray-200 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900">
                <div>
                  <p className="text-sm font-medium text-gray-900 dark:text-white">
                    Need a human?
                  </p>
                  <p className="text-xs text-gray-600 dark:text-gray-400">
                    Open the admin chat for account-specific help.
                  </p>
                </div>
                <button
                  className="rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white transition hover:bg-primary-hover"
                  type="button"
                  onClick={handleSelectAdmin}
                >
                  Talk to admin
                </button>
              </div>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Talk to Admin */}
              <button
                className="p-6 border border-gray-300 dark:border-gray-700 hover:border-primary dark:hover:border-primary bg-white dark:bg-gray-900 hover:bg-gray-50 dark:hover:bg-gray-800 transition-all duration-200 group"
                onClick={handleSelectAdmin}
              >
                <div className="flex flex-col items-center gap-4 text-center">
                  <div className="w-16 h-16 bg-gray-100 dark:bg-gray-800 group-hover:bg-primary/10 rounded-full flex items-center justify-center transition-colors">
                    <HiOutlineUser className="w-8 h-8 text-primary" />
                  </div>
                  <div>
                    <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                      Talk to Admin
                    </h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      Connect with our support team for personalized assistance
                    </p>
                  </div>
                </div>
              </button>

              {/* AI Chat */}
              <button
                className="p-6 border border-gray-300 dark:border-gray-700 hover:border-primary dark:hover:border-primary bg-white dark:bg-gray-900 hover:bg-gray-50 dark:hover:bg-gray-800 transition-all duration-200 group"
                onClick={handleSelectAI}
              >
                <div className="flex flex-col items-center gap-4 text-center">
                  <div className="w-16 h-16 bg-gray-100 dark:bg-gray-800 group-hover:bg-primary/10 rounded-full flex items-center justify-center transition-colors">
                    <HiOutlineChatBubbleLeftRight className="w-8 h-8 text-primary" />
                  </div>
                  <div>
                    <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                      AI Assistant
                    </h3>
                    <p className="text-sm text-gray-600 dark:text-gray-400">
                      Get instant answers about orders, returns, and policies
                    </p>
                  </div>
                </div>
              </button>
            </div>
          )}
        </div>
      </Modal>
    </>
  );
}
