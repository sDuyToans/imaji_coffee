import { ReactElement, useEffect, useMemo, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Client, Frame, IMessage, StompSubscription } from "@stomp/stompjs";
import { Spinner } from "@heroui/spinner";

import { useGetMeQuery } from "@/api/account/accountApi.ts";
import DefaultLayout from "@/layouts/default.tsx";
import {
  ChatAdminNotificationDto,
  ChatConversationDto,
  ChatMessageDto,
  ChatSenderType,
  SendChatMessageRequest,
  useCreateConversationByCustomerIdMutation,
  useLazyGetAdminConversationsQuery,
  useCreateMyCurrentConversationMutation,
  useLazyGetConversationMessagesPageQuery,
  useLazyGetMyCurrentConversationQuery,
  useCloseConversationMutation,
  useMarkConversationAsReadMutation,
  useReassignConversationMutation,
  useSendConversationMessageMutation,
} from "@/api/chat/chatApi.ts";

const CHAT_API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1";
const WEBSOCKET_RECONNECT_DELAY = 5000;

type RenderableMessage = ChatMessageDto & {
  key: string;
  fingerprint: string;
};

type AdminNotificationItem = {
  key: string;
  conversationId: number;
  senderName: string;
  messagePreview: string;
  createdAt: string;
};

export default function Chat(): ReactElement {
  return (
    <DefaultLayout>
      <ChatScreen />
    </DefaultLayout>
  );
}

function ChatScreen(): ReactElement {
  const { data: me, isLoading: isMeLoading } = useGetMeQuery();
  const [loadCurrentConversation] = useLazyGetMyCurrentConversationQuery();
  const [createCurrentConversation] = useCreateMyCurrentConversationMutation();
  const [createConversationByCustomerId] =
    useCreateConversationByCustomerIdMutation();
  const [loadConversationMessagesPage] =
    useLazyGetConversationMessagesPageQuery();
  const [markConversationAsRead] = useMarkConversationAsReadMutation();
  const [loadAdminConversations] = useLazyGetAdminConversationsQuery();
  const [reassignConversation] = useReassignConversationMutation();
  const [closeConversation] = useCloseConversationMutation();
  const [sendConversationMessage] = useSendConversationMessageMutation();

  const [conversation, setConversation] = useState<ChatConversationDto | null>(
    null,
  );
  const [adminConversations, setAdminConversations] = useState<
    ChatConversationDto[]
  >([]);
  const [messages, setMessages] = useState<RenderableMessage[]>([]);
  const [draft, setDraft] = useState("");
  const [isConversationLoading, setIsConversationLoading] = useState(true);
  const [isMessagesLoading, setIsMessagesLoading] = useState(false);
  const [isStartingNewConversation, setIsStartingNewConversation] =
    useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [socketError, setSocketError] = useState<string | null>(null);
  const [adminNotification, setAdminNotification] = useState<string | null>(
    null,
  );
  const [userNotification, setUserNotification] = useState<string | null>(null);
  const [adminNotifications, setAdminNotifications] = useState<
    AdminNotificationItem[]
  >([]);
  const [unreadByConversation, setUnreadByConversation] = useState<
    Record<number, number>
  >({});
  const [isConnected, setIsConnected] = useState(false);

  const clientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<StompSubscription[]>([]);
  const adminNotificationTimeoutRef = useRef<number | null>(null);
  const userNotificationTimeoutRef = useRef<number | null>(null);
  const messagesContainerRef = useRef<HTMLDivElement | null>(null);
  const shouldAutoScrollRef = useRef(true);
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  const senderName = me?.username ?? "You";
  const isAdmin = me?.roles?.toUpperCase().includes("ADMIN") ?? false;
  const senderType: ChatSenderType = useMemo(
    () => (isAdmin ? "ADMIN" : "USER"),
    [isAdmin],
  );
  const conversationStatus = (conversation?.status ?? "").toUpperCase();
  const isChatLive =
    conversationStatus === "OPEN" ||
    conversationStatus === "PENDING" ||
    conversationStatus === "WAITING";
  const isChatActive =
    conversationStatus === "OPEN" || conversationStatus === "PENDING";
  const isChatWaiting = conversationStatus === "WAITING";
  const isChatClosed = conversationStatus === "CLOSED";
  const shouldConnectSocket = isAdmin || (!!conversation?.id && isChatLive);
  const currentUserId = useMemo(() => {
    const rawId =
      (me as { userId?: number | string; id?: number | string } | undefined)
        ?.userId ??
      (me as { userId?: number | string; id?: number | string } | undefined)
        ?.id;

    if (rawId == null) {
      return null;
    }
    const parsed = Number(rawId);

    return Number.isFinite(parsed) ? parsed : null;
  }, [me]);
  const adminInboxKey = currentUserId ?? 0;

  useEffect(() => {
    let active = true;

    const initializeConversation = async (): Promise<void> => {
      setIsConversationLoading(true);
      setLoadError(null);

      if (isAdmin) {
        try {
          const list = await loadAdminConversations(adminInboxKey).unwrap();

          if (!active) {
            return;
          }

          setAdminConversations(list);
          setUnreadByConversation(
            Object.fromEntries(
              list
                .filter((item) => (item.unreadCount ?? 0) > 0)
                .map((item) => [item.id, item.unreadCount ?? 0]),
            ),
          );
          const nextConversation = list[0] ?? null;

          setConversation((current) => {
            if (current && list.some((item) => item.id === current.id)) {
              return list.find((item) => item.id === current.id) ?? current;
            }

            return nextConversation;
          });

          if (!nextConversation) {
            try {
              const assigned = await loadCurrentConversation().unwrap();

              if (!active) {
                return;
              }
              setConversation(assigned);
              setAdminConversations((current) => {
                if (current.some((item) => item.id === assigned.id)) {
                  return current;
                }

                return [assigned, ...current];
              });
            } catch (error) {
              if (!isNotFound(error) && active) {
                setLoadError("Unable to load admin conversations right now.");
              }
            }
          }
        } catch {
          if (active) {
            setLoadError("Unable to load admin conversations right now.");
          }
        } finally {
          if (active) {
            setIsConversationLoading(false);
          }
        }

        return;
      }

      try {
        const existing = await loadCurrentConversation().unwrap();

        if (active) {
          setConversation(existing);
          setSocketError(null);
        }
      } catch (error) {
        try {
          const created = await startConversationWithFallback();

          if (active) {
            setConversation(created);
            setSocketError(null);
          }
        } catch (startError) {
          if (active) {
            setLoadError(
              extractErrorMessage(
                startError,
                "Unable to start your chat right now.",
              ),
            );
          }
        }
      } finally {
        if (active) {
          setIsConversationLoading(false);
        }
      }
    };

    void initializeConversation();

    return () => {
      active = false;
    };
  }, [
    createCurrentConversation,
    adminInboxKey,
    isAdmin,
    loadAdminConversations,
    loadCurrentConversation,
  ]);

  useEffect(() => {
    if (!isAdmin || isConversationLoading) {
      return undefined;
    }

    let active = true;

    const loadConversations = async (): Promise<void> => {
      try {
        const list = await loadAdminConversations(adminInboxKey).unwrap();

        if (!active) {
          return;
        }
        setLoadError(null);
        setUnreadByConversation(
          Object.fromEntries(
            list
              .filter((item) => (item.unreadCount ?? 0) > 0)
              .map((item) => [item.id, item.unreadCount ?? 0]),
          ),
        );
        setAdminConversations(list);
        setConversation((current) => {
          if (current && list.some((item) => item.id === current.id)) {
            return list.find((item) => item.id === current.id) ?? current;
          }

          return list[0] ?? null;
        });
      } catch {
        // Keep existing UI state; the next socket event or manual refresh may recover.
      }
    };

    void loadConversations();

    return () => {
      active = false;
    };
  }, [isAdmin, isConversationLoading, loadAdminConversations, adminInboxKey]);

  useEffect(() => {
    if (!conversation?.id) {
      setMessages([]);

      return;
    }

    let active = true;

    const fetchMessages = async (): Promise<void> => {
      setIsMessagesLoading(true);

      try {
        const history = await loadConversationMessagesPage({
          conversationId: conversation.id,
          page: 0,
          size: 50,
        }).unwrap();

        if (!active) {
          return;
        }

        setMessages(sortMessages(history.content));
        setSocketError(null);
      } catch {
        if (active) {
          setMessages([]);
        }
      } finally {
        if (active) {
          setIsMessagesLoading(false);
        }
      }
    };

    void fetchMessages();

    return () => {
      active = false;
    };
  }, [conversation?.id, conversationStatus, loadConversationMessagesPage]);

  useEffect(() => {
    const syncVisibleState = (): void => {
      if (document.visibilityState !== "visible") {
        return;
      }

      if (isAdmin) {
        void refreshAdminConversations(conversation?.id ?? undefined);
      }

      if (conversation?.id) {
        void refreshCurrentConversationMessages(conversation.id);
      }
    };

    window.addEventListener("focus", syncVisibleState);
    document.addEventListener("visibilitychange", syncVisibleState);

    return () => {
      window.removeEventListener("focus", syncVisibleState);
      document.removeEventListener("visibilitychange", syncVisibleState);
    };
  }, [conversation?.id, isAdmin]);

  useEffect(() => {
    if (!shouldConnectSocket) {
      return;
    }

    const socket = new SockJS(CHAT_API_BASE_URL, undefined, {
      withCredentials: true,
    } as unknown as SockJS.Options);

    const client = new Client({
      webSocketFactory: () => socket as unknown as WebSocket,
      reconnectDelay: WEBSOCKET_RECONNECT_DELAY,
      connectHeaders: getConnectHeaders(),
      debug: () => undefined,
      onConnect: () => {
        setIsConnected(true);
        setSocketError(null);

        subscriptionsRef.current.forEach((subscription) =>
          subscription.unsubscribe(),
        );
        subscriptionsRef.current = [];

        if (conversation?.id) {
          subscriptionsRef.current.push(
            client.subscribe(
              `/user/queue/chat/messages/${conversation.id}`,
              (message: IMessage) => {
                try {
                  const parsed = JSON.parse(message.body) as ChatMessageDto;

                  setMessages((current) =>
                    mergeMessages(current, [normalizeMessage(parsed)]),
                  );
                } catch {
                  setSocketError("Received an unreadable chat message.");
                }
              },
            ),
          );
        }

        subscriptionsRef.current.push(
          client.subscribe("/user/queue/errors", (message: IMessage) => {
            setSocketError(
              message.body || "An error occurred while sending the message.",
            );
          }),
        );

        subscriptionsRef.current.push(
          client.subscribe(
            "/user/queue/chat/notifications",
            (message: IMessage) => {
              try {
                const payload = JSON.parse(
                  message.body,
                ) as ChatAdminNotificationDto;

                handleNotification(payload);
              } catch {
                setSocketError("Received an unreadable chat notification.");
              }
            },
          ),
        );

        if (isAdmin) {
          subscriptionsRef.current.push(
            client.subscribe(
              "/api/v1/topic/chat/admin/notifications",
              (message: IMessage) => {
                try {
                  const payload = JSON.parse(
                    message.body,
                  ) as ChatAdminNotificationDto;

                  handleNotification(payload);
                } catch {
                  setSocketError(
                    "Received an unreadable admin broadcast notification.",
                  );
                }
              },
            ),
          );
          void refreshAdminConversations();
        }

        if (!isAdmin && conversation?.id) {
          void refreshCurrentConversationMessages(conversation.id);
        }
      },
      onDisconnect: () => {
        setIsConnected(false);
      },
      onWebSocketClose: () => {
        setIsConnected(false);
      },
      onWebSocketError: () => {
        setSocketError("Websocket connection failed.");
        setIsConnected(false);
      },
      onStompError: (frame: Frame) => {
        setSocketError(frame.headers.message || "Chat connection failed.");
      },
    });

    clientRef.current = client;
    client.activate();

    return () => {
      subscriptionsRef.current.forEach((subscription) =>
        subscription.unsubscribe(),
      );
      subscriptionsRef.current = [];
      void client.deactivate();
      clientRef.current = null;
      setIsConnected(false);
    };
  }, [adminInboxKey, conversation?.id, isAdmin, shouldConnectSocket]);

  useEffect(() => {
    return () => {
      if (adminNotificationTimeoutRef.current !== null) {
        window.clearTimeout(adminNotificationTimeoutRef.current);
        adminNotificationTimeoutRef.current = null;
      }
      if (userNotificationTimeoutRef.current !== null) {
        window.clearTimeout(userNotificationTimeoutRef.current);
        userNotificationTimeoutRef.current = null;
      }
    };
  }, []);

  useEffect(() => {
    if (!messagesContainerRef.current || !shouldAutoScrollRef.current) {
      return;
    }

    const container = messagesContainerRef.current;

    container.scrollTop = container.scrollHeight;
  }, [conversation?.id, messages, isMessagesLoading]);

  const statusCopy = useMemo(() => {
    if (isConversationLoading || isMeLoading) {
      return {
        label: "Loading",
        tone: "border-yellow-200 bg-yellow-50 text-yellow-700",
      };
    }

    if (isChatWaiting) {
      return {
        label: "Waiting",
        tone: "border-yellow-200 bg-yellow-50 text-yellow-700",
      };
    }

    if (isAdmin && !conversation?.id) {
      return {
        label: "Waiting for assignment",
        tone: "border-blue-200 bg-blue-50 text-blue-700",
      };
    }

    if (isChatClosed) {
      return {
        label: "Closed",
        tone: "border-gray-200 bg-gray-100 text-gray-700",
      };
    }

    return {
      label: isConnected ? "Connected" : "Reconnecting",
      tone: isConnected
        ? "border-green-200 bg-green-50 text-green-700"
        : "border-blue-200 bg-blue-50 text-blue-700",
    };
  }, [
    conversation?.id,
    isAdmin,
    isChatClosed,
    isChatWaiting,
    isConnected,
    isConversationLoading,
    isMeLoading,
  ]);

  const canSend = isChatActive && !isConversationLoading;

  const handleSendMessage = (): void => {
    const content = draft.trim();

    if (!content || !conversation?.id || !canSend) {
      return;
    }

    const payload: SendChatMessageRequest = {
      content,
      senderName,
      senderType,
    };

    void sendConversationMessage({
      conversationId: conversation.id,
      payload,
    })
      .unwrap()
      .then((savedMessage) => {
        setMessages((current) =>
          mergeMessages(current, [normalizeMessage(savedMessage)]),
        );
        shouldAutoScrollRef.current = true;
        setDraft("");
        setSocketError(null);
      })
      .catch((error) => {
        setSocketError(extractErrorMessage(error, "Unable to send message."));
      });
  };

  const handleNotification = (payload: ChatAdminNotificationDto): void => {
    if (payload.conversationId == null) {
      return;
    }

    const type = payload.notificationType?.toUpperCase();

    if (
      isAdmin &&
      (type === "USER_MESSAGE" ||
        type === "USER_MESSAGE_BROADCAST" ||
        type === "CONVERSATION_CREATED" ||
        type === "CONVERSATION_ASSIGNED" ||
        type === "CONVERSATION_UPDATED")
    ) {
      setSocketError(null);
      showAdminNotification(payload);
      if (conversation?.id === payload.conversationId) {
        void refreshCurrentConversationMessages(payload.conversationId);
      }
      void refreshAdminConversations(payload.conversationId);

      return;
    }

    if (!isAdmin && type === "ADMIN_MESSAGE") {
      setSocketError(null);
      showUserNotification(payload);
      if (conversation?.id === payload.conversationId) {
        void refreshCurrentConversationMessages(payload.conversationId);
      }

      return;
    }

    if (!isAdmin && type === "CONVERSATION_CLOSED") {
      setSocketError(null);
      if (conversation?.id === payload.conversationId) {
        setConversation((current) =>
          current
            ? {
                ...current,
                status: "CLOSED",
                updatedAt: payload.createdAt ?? current.updatedAt,
              }
            : current,
        );
      }
      setUserNotification("This conversation has been marked as solved.");
    }
  };

  const refreshAdminConversations = async (
    preferredConversationId?: number,
  ): Promise<void> => {
    if (!isAdmin) {
      return;
    }

    try {
      const list = await loadAdminConversations(adminInboxKey).unwrap();

      setUnreadByConversation(
        Object.fromEntries(
          list
            .filter((item) => (item.unreadCount ?? 0) > 0)
            .map((item) => [item.id, item.unreadCount ?? 0]),
        ),
      );
      setAdminConversations(list);
      setConversation((current) => {
        if (preferredConversationId != null) {
          const preferred = list.find(
            (item) => item.id === preferredConversationId,
          );

          if (preferred) {
            return preferred;
          }
        }
        if (current && list.some((item) => item.id === current.id)) {
          return list.find((item) => item.id === current.id) ?? current;
        }

        return list[0] ?? null;
      });
      setLoadError(null);
      setSocketError(null);
    } catch {
      // Keep admin UI usable; websocket events or a manual refresh can retry later.
    }
  };

  const refreshCurrentConversationMessages = async (
    conversationId: number,
  ): Promise<void> => {
    try {
      const history = await loadConversationMessagesPage({
        conversationId,
        page: 0,
        size: 50,
      }).unwrap();
      const refreshedMessages = sortMessages(history.content);

      setMessages((current) => mergeMessages(current, refreshedMessages));
      await markConversationAsRead(conversationId).unwrap();
      setSocketError(null);
    } catch {
      // Keep the current thread visible; the next socket event or visibility change can retry.
    }
  };

  const showAdminNotification = (payload: ChatAdminNotificationDto): void => {
    if (payload.conversationId == null) {
      return;
    }

    const sender = payload.senderName?.trim() || "User";
    const preview = payload.messagePreview?.trim();
    const text = preview
      ? `New message from ${sender} in #${payload.conversationId}: ${preview}`
      : `New message from ${sender} in #${payload.conversationId}.`;
    const createdAt = payload.createdAt ?? new Date().toISOString();

    setAdminNotification(text);
    setAdminNotifications((current) => {
      const key =
        payload.messageId != null
          ? `${payload.conversationId}-${payload.messageId}`
          : `${payload.conversationId}-${createdAt}-${sender}`;

      if (current.some((item) => item.key === key)) {
        return current;
      }

      const nextItem: AdminNotificationItem = {
        key,
        conversationId: payload.conversationId,
        senderName: sender,
        messagePreview: preview || "(No preview)",
        createdAt,
      };

      return [nextItem, ...current].slice(0, 20);
    });

    if (adminNotificationTimeoutRef.current !== null) {
      window.clearTimeout(adminNotificationTimeoutRef.current);
    }
    adminNotificationTimeoutRef.current = window.setTimeout(() => {
      setAdminNotification(null);
      adminNotificationTimeoutRef.current = null;
    }, 6000);
  };

  useEffect(() => {
    if (!isAdmin) {
      return;
    }

    setAdminNotifications((current) => {
      const generated = adminConversations
        .filter((item) => item.customerId != null)
        .map((item) => {
          const createdAt = item.updatedAt ?? new Date().toISOString();

          return {
            key: `conversation-${item.id}-${createdAt}`,
            conversationId: item.id,
            senderName: `Customer #${item.customerId}`,
            messagePreview:
              item.status?.toUpperCase() === "WAITING"
                ? "New waiting chat."
                : "Conversation updated.",
            createdAt,
          } as AdminNotificationItem;
        });

      const merged = [...current];

      for (const next of generated) {
        if (!merged.some((item) => item.key === next.key)) {
          merged.push(next);
        }
      }

      return merged
        .sort(
          (left, right) =>
            new Date(right.createdAt).getTime() -
            new Date(left.createdAt).getTime(),
        )
        .slice(0, 20);
    });
  }, [adminConversations, isAdmin]);

  const showUserNotification = (payload: ChatAdminNotificationDto): void => {
    const sender = payload.senderName?.trim() || "Admin";
    const preview = payload.messagePreview?.trim();
    const text = preview
      ? `New reply from ${sender}: ${preview}`
      : `New reply from ${sender}.`;

    setUserNotification(text);
    if (userNotificationTimeoutRef.current !== null) {
      window.clearTimeout(userNotificationTimeoutRef.current);
    }
    userNotificationTimeoutRef.current = window.setTimeout(() => {
      setUserNotification(null);
      userNotificationTimeoutRef.current = null;
    }, 6000);
  };

  const handleMessagesScroll = (): void => {
    const container = messagesContainerRef.current;

    if (!container) {
      return;
    }

    const remaining =
      container.scrollHeight - container.scrollTop - container.clientHeight;

    shouldAutoScrollRef.current = remaining < 80;
  };

  const handleKeyDown = (event: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      handleSendMessage();
    }
  };

  const clearUnreadForCurrentConversation = (): void => {
    const conversationId = conversation?.id;

    if (!conversationId) {
      return;
    }
    clearUnreadForConversation(conversationId);
    void markConversationAsRead(conversationId).unwrap();
  };

  const clearUnreadForConversation = (conversationId: number): void => {
    setUnreadByConversation((current) => {
      if (!current[conversationId]) {
        return current;
      }
      const next = { ...current };

      delete next[conversationId];

      return next;
    });
  };

  const handleSelectConversation = (
    nextConversation: ChatConversationDto,
  ): void => {
    if (
      isAdmin &&
      currentUserId != null &&
      nextConversation.assignedAdminId !== currentUserId
    ) {
      void (async () => {
        try {
          const claimed = await reassignConversation({
            conversationId: nextConversation.id,
            targetAdminId: currentUserId,
          }).unwrap();

          setAdminConversations((current) =>
            current.map((item) => (item.id === claimed.id ? claimed : item)),
          );
          setConversation(claimed);
          clearUnreadForConversation(claimed.id);
          void refreshCurrentConversationMessages(claimed.id);
          shouldAutoScrollRef.current = true;
          setDraft("");
        } catch {
          setSocketError("Unable to open this conversation right now.");
        }
      })();

      return;
    }

    setConversation(nextConversation);
    clearUnreadForConversation(nextConversation.id);
    void refreshCurrentConversationMessages(nextConversation.id);
    shouldAutoScrollRef.current = true;
    setDraft("");
  };

  const handleOpenNotificationConversation = (conversationId: number): void => {
    const matched = adminConversations.find(
      (item) => item.id === conversationId,
    );

    if (matched) {
      handleSelectConversation(matched);

      return;
    }
    void (async () => {
      if (currentUserId == null) {
        void refreshAdminConversations(conversationId);

        return;
      }
      try {
        const claimed = await reassignConversation({
          conversationId,
          targetAdminId: currentUserId,
        }).unwrap();

        setAdminConversations((current) => {
          if (current.some((item) => item.id === claimed.id)) {
            return current.map((item) =>
              item.id === claimed.id ? claimed : item,
            );
          }

          return [claimed, ...current];
        });
        handleSelectConversation(claimed);
      } catch {
        void refreshAdminConversations(conversationId);
      }
    })();
  };

  const handleMarkAsSolved = (): void => {
    if (!isAdmin || !conversation?.id) {
      return;
    }

    void (async () => {
      try {
        await closeConversation(conversation.id).unwrap();
        setUnreadByConversation((current) => {
          if (!current[conversation.id]) {
            return current;
          }
          const next = { ...current };

          delete next[conversation.id];

          return next;
        });
        await refreshAdminConversations();
      } catch {
        setSocketError("Unable to mark conversation as solved.");
      }
    })();
  };

  const handleStartNewConversationForUser = (): void => {
    if (isAdmin || isStartingNewConversation) {
      return;
    }

    void (async () => {
      try {
        setIsStartingNewConversation(true);
        const nextConversation = await startConversationWithFallback();

        setConversation(nextConversation);
        setMessages([]);
        setDraft("");
        setLoadError(null);
        setSocketError(null);
        shouldAutoScrollRef.current = true;
      } catch (error) {
        setLoadError(
          extractErrorMessage(
            error,
            "Unable to start a new conversation right now.",
          ),
        );
      } finally {
        setIsStartingNewConversation(false);
      }
    })();
  };

  const startConversationWithFallback =
    async (): Promise<ChatConversationDto> => {
      try {
        return await createCurrentConversation().unwrap();
      } catch (createError) {
        try {
          return await loadCurrentConversation().unwrap();
        } catch (loadError) {
          if (currentUserId != null) {
            return await createConversationByCustomerId(currentUserId).unwrap();
          }
          throw loadError ?? createError;
        }
      }
    };

  if (isMeLoading || isConversationLoading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <Spinner color="primary" />
      </div>
    );
  }

  if (loadError && !isAdmin) {
    return (
      <StatusPanel
        action={
          <button
            className="mt-5 rounded-lg border border-primary px-4 py-2 text-sm font-medium text-primary transition hover:bg-primary/10 disabled:cursor-not-allowed disabled:opacity-50"
            disabled={isStartingNewConversation}
            type="button"
            onClick={handleStartNewConversationForUser}
          >
            {isStartingNewConversation ? "Starting..." : "Start conversation"}
          </button>
        }
        description={loadError}
        title="Chat unavailable"
      />
    );
  }

  return (
    <section className="mx-auto flex w-full max-w-6xl flex-col gap-6 px-4 py-8 lg:px-6">
      <header className="flex flex-col gap-4 rounded-2xl border border-gray-200 bg-white p-5 shadow-sm dark:border-gray-800 dark:bg-gray-900">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400">
              Logged in as {senderName}
            </p>
            <h1 className="mt-1 text-3xl font-medium text-gray-900 dark:text-white">
              Support chat
            </h1>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <span
              className={`rounded-full border px-3 py-1 text-xs font-medium ${statusCopy.tone}`}
            >
              {statusCopy.label}
            </span>
            <span className="rounded-full border border-gray-200 px-3 py-1 text-xs text-gray-600 dark:border-gray-700 dark:text-gray-300">
              Conversation #{conversation?.id ?? "—"}
            </span>
          </div>
        </div>

        {isChatWaiting && (
          <InfoBanner
            description={
              conversation?.waitMessage ||
              "Please wait while the support team becomes available."
            }
            title="You’re in the queue"
            value={
              conversation?.queuePosition != null
                ? `Queue position ${conversation.queuePosition}`
                : undefined
            }
          />
        )}

        {isChatClosed && (
          <InfoBanner
            action={
              !isAdmin ? (
                <button
                  className="mt-3 rounded-lg border border-primary px-3 py-1.5 text-sm font-medium text-primary transition hover:bg-primary/10 disabled:cursor-not-allowed disabled:opacity-50"
                  disabled={isStartingNewConversation}
                  type="button"
                  onClick={handleStartNewConversationForUser}
                >
                  {isStartingNewConversation
                    ? "Starting..."
                    : "Start new conversation"}
                </button>
              ) : undefined
            }
            description="You can review the history below, but sending new messages is disabled."
            title="This conversation is closed"
          />
        )}

        {socketError && isChatActive && (
          <InfoBanner description={socketError} title="Connection issue" />
        )}

        {loadError && isAdmin && (
          <InfoBanner description={loadError} title="Sync issue" />
        )}

        {adminNotification && isAdmin && (
          <InfoBanner
            description={adminNotification}
            title="New user message"
          />
        )}

        {userNotification && !isAdmin && (
          <InfoBanner description={userNotification} title="New admin reply" />
        )}
      </header>

      <div
        className={`grid gap-6 ${
          isAdmin
            ? "lg:grid-cols-[280px_minmax(0,1fr)_280px]"
            : "lg:grid-cols-[minmax(0,1fr)_280px]"
        }`}
      >
        {isAdmin && (
          <aside className="flex flex-col gap-4">
            <SideCard title="Conversations">
              {adminConversations.length === 0 ? (
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  No assigned conversations yet.
                </p>
              ) : (
                <ul className="space-y-2">
                  {adminConversations.map((item) => {
                    const isSelected = conversation?.id === item.id;
                    const unreadCount = unreadByConversation[item.id] ?? 0;

                    return (
                      <li key={item.id}>
                        <button
                          className={`w-full rounded-xl border p-3 text-left transition ${
                            isSelected
                              ? "border-primary bg-primary/10"
                              : "border-gray-200 bg-gray-50 hover:border-primary/60 dark:border-gray-700 dark:bg-gray-950"
                          }`}
                          type="button"
                          onClick={() => handleSelectConversation(item)}
                        >
                          <p className="text-sm font-medium text-gray-900 dark:text-white">
                            Customer #{item.customerId ?? "—"}
                          </p>
                          <p className="mt-1 text-xs text-gray-500 dark:text-gray-400">
                            Chat #{item.id} • {item.status}
                            {unreadCount > 0 && (
                              <span className="ml-2 inline-flex min-h-5 min-w-5 items-center justify-center rounded-full bg-red-500 px-1.5 text-[11px] font-semibold text-white">
                                {unreadCount > 99 ? "99+" : unreadCount}
                              </span>
                            )}
                          </p>
                          <p className="mt-1 text-xs text-gray-500 dark:text-gray-400">
                            Updated{" "}
                            {item.updatedAt ? formatTime(item.updatedAt) : "—"}
                          </p>
                        </button>
                      </li>
                    );
                  })}
                </ul>
              )}
            </SideCard>
          </aside>
        )}

        <main className="flex h-[65vh] min-h-[65vh] flex-col overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm dark:border-gray-800 dark:bg-gray-900">
          <div
            ref={messagesContainerRef}
            className="flex-1 overflow-y-auto p-4 sm:p-6"
            onScroll={handleMessagesScroll}
          >
            {messages.length === 0 ? (
              <EmptyChatState
                hasConversation={!!conversation?.id}
                isAdmin={isAdmin}
                isClosed={isChatClosed}
                isLoading={isMessagesLoading}
                isWaiting={isChatWaiting}
              />
            ) : (
              <ul className="flex flex-col gap-4">
                {messages.map((message) => (
                  <ChatMessageItem
                    key={message.key}
                    currentSenderName={senderName}
                    currentSenderType={senderType}
                    message={message}
                  />
                ))}
                <div ref={messagesEndRef} />
              </ul>
            )}
          </div>

          <div className="border-t border-gray-200 p-4 dark:border-gray-800">
            <div className="flex gap-3">
              <textarea
                className="min-h-[56px] flex-1 resize-none rounded-xl border border-gray-300 bg-white px-4 py-3 text-gray-900 outline-none transition focus:border-primary dark:border-gray-700 dark:bg-gray-950 dark:text-white"
                disabled={!canSend}
                placeholder={
                  isChatWaiting
                    ? "Waiting for an agent..."
                    : isChatClosed
                      ? "This conversation is closed"
                      : "Type a message..."
                }
                value={draft}
                onChange={(event) => setDraft(event.target.value)}
                onFocus={clearUnreadForCurrentConversation}
                onKeyDown={handleKeyDown}
              />
              <button
                className="h-[56px] rounded-xl bg-primary px-6 text-sm font-medium text-white transition hover:bg-primary-hover disabled:cursor-not-allowed disabled:opacity-40"
                disabled={!canSend || !draft.trim()}
                type="button"
                onClick={handleSendMessage}
              >
                Send
              </button>
            </div>
          </div>
        </main>

        <aside className="flex flex-col gap-4">
          <SideCard title="Conversation details">
            <DetailRow label="Status" value={conversation?.status ?? "—"} />
            <DetailRow
              label="Queue"
              value={conversation?.queuePosition?.toString() ?? "—"}
            />
            <DetailRow
              label="Assigned admin"
              value={conversation?.assignedAdminId?.toString() ?? "—"}
            />
            <DetailRow label="Messages" value={messages.length.toString()} />
            {isAdmin && conversation?.id && !isChatClosed && (
              <button
                className="mt-2 w-full rounded-xl border border-green-600 bg-green-50 px-3 py-2 text-sm font-medium text-green-700 transition hover:bg-green-100 dark:border-green-500 dark:bg-green-950 dark:text-green-300"
                type="button"
                onClick={handleMarkAsSolved}
              >
                Mark as solved
              </button>
            )}
          </SideCard>

          <SideCard title="How it works">
            <p className="text-sm leading-6 text-gray-600 dark:text-gray-400">
              Open chats connect live. Waiting chats stay read-only until an
              agent picks them up. Closed chats stay archived.
            </p>
          </SideCard>

          {isAdmin && (
            <SideCard title="Recent user notifications">
              {adminNotifications.length === 0 ? (
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  No user notifications yet.
                </p>
              ) : (
                <ul
                  className={`space-y-3 ${
                    adminNotifications.length > 5
                      ? "max-h-[360px] overflow-y-auto pr-1"
                      : ""
                  }`}
                >
                  {adminNotifications.map((item) => (
                    <li key={item.key}>
                      <button
                        className="w-full rounded-xl border border-gray-200 bg-gray-50 p-3 text-left transition hover:border-primary/60 dark:border-gray-700 dark:bg-gray-950"
                        type="button"
                        onClick={() =>
                          handleOpenNotificationConversation(
                            item.conversationId,
                          )
                        }
                      >
                        <p className="text-xs text-gray-500 dark:text-gray-400">
                          #{item.conversationId} • {formatTime(item.createdAt)}
                        </p>
                        <p className="mt-1 text-sm font-medium text-gray-900 dark:text-white">
                          {item.senderName}
                        </p>
                        <p className="mt-1 text-sm text-gray-700 dark:text-gray-300">
                          {item.messagePreview}
                        </p>
                      </button>
                    </li>
                  ))}
                </ul>
              )}
            </SideCard>
          )}
        </aside>
      </div>
    </section>
  );
}

function sortMessages(messages: ChatMessageDto[]): RenderableMessage[] {
  const normalized = messages.map((message, index) =>
    normalizeMessage(message, index),
  );

  return mergeRenderableMessages(normalized);
}

function normalizeMessage(
  message: ChatMessageDto,
  fallbackIndex = 0,
): RenderableMessage {
  const createdAt = message.createdAt ?? new Date().toISOString();
  const fingerprint = createMessageFingerprint(message, createdAt);
  const key =
    message.id != null
      ? `message-${message.id}`
      : `${fingerprint}-${fallbackIndex}`;

  return {
    ...message,
    createdAt,
    key,
    fingerprint,
  };
}

function mergeMessages(
  current: RenderableMessage[],
  incoming: RenderableMessage[],
): RenderableMessage[] {
  const map = new Map<string, RenderableMessage>();
  const fingerprintsWithPersistedId = new Set<string>();
  const allItems = [...current, ...incoming];

  for (const item of allItems) {
    if (item.id != null) {
      fingerprintsWithPersistedId.add(item.fingerprint);
      map.delete(`fp:${item.fingerprint}`);
      map.set(`id:${item.id}`, item);
    }
  }

  for (const item of allItems) {
    if (item.id != null) {
      continue;
    }
    if (fingerprintsWithPersistedId.has(item.fingerprint)) {
      continue;
    }
    map.set(`fp:${item.fingerprint}`, item);
  }

  return mergeRenderableMessages(Array.from(map.values()));
}

function mergeRenderableMessages(
  messages: RenderableMessage[],
): RenderableMessage[] {
  return messages.sort((left, right) => {
    const leftTime = left.createdAt ? new Date(left.createdAt).getTime() : 0;
    const rightTime = right.createdAt ? new Date(right.createdAt).getTime() : 0;

    return leftTime - rightTime;
  });
}

function createMessageFingerprint(
  message: Pick<ChatMessageDto, "content" | "senderName" | "senderType">,
  createdAt: string,
): string {
  const bucket = Math.floor(new Date(createdAt).getTime() / 5000);

  return `${message.senderName}|${message.senderType}|${message.content}|${bucket}`;
}

function isNotFound(error: unknown): boolean {
  return (
    typeof error === "object" &&
    error !== null &&
    "status" in error &&
    (error as { status?: number }).status === 404
  );
}

function extractErrorMessage(error: unknown, fallback: string): string {
  if (
    typeof error === "object" &&
    error !== null &&
    "data" in error &&
    typeof (error as { data?: unknown }).data === "object" &&
    (error as { data?: unknown }).data !== null &&
    "message" in ((error as { data?: { message?: unknown } }).data ?? {}) &&
    typeof (error as { data?: { message?: unknown } }).data?.message ===
      "string"
  ) {
    return (error as { data: { message: string } }).data.message;
  }

  if (
    typeof error === "object" &&
    error !== null &&
    "error" in error &&
    typeof (error as { error?: unknown }).error === "string"
  ) {
    return (error as { error: string }).error;
  }

  return fallback;
}

function getConnectHeaders(): Record<string, string> {
  const token = localStorage.getItem("token");

  if (!token) {
    return {};
  }

  return {
    Authorization: `Bearer ${token}`,
  };
}

function formatTime(timestamp: string): string {
  try {
    return new Date(timestamp).toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch {
    return "";
  }
}

function StatusPanel({
  title,
  description,
  action,
}: Readonly<{
  title: string;
  description: string;
  action?: React.ReactNode;
}>): ReactElement {
  return (
    <div className="mx-auto flex min-h-[50vh] w-full max-w-3xl items-center justify-center px-4 py-10">
      <div className="w-full rounded-2xl border border-gray-200 bg-white p-8 text-center shadow-sm dark:border-gray-800 dark:bg-gray-900">
        <h2 className="text-2xl font-medium text-gray-900 dark:text-white">
          {title}
        </h2>
        <p className="mt-3 text-gray-600 dark:text-gray-400">{description}</p>
        {action}
      </div>
    </div>
  );
}

function EmptyChatState({
  isAdmin,
  hasConversation,
  isWaiting,
  isClosed,
  isLoading,
}: Readonly<{
  isAdmin: boolean;
  hasConversation: boolean;
  isWaiting: boolean;
  isClosed: boolean;
  isLoading: boolean;
}>): ReactElement {
  const title = isLoading
    ? "Loading messages..."
    : isAdmin && !hasConversation
      ? "Waiting for assigned chats"
      : isWaiting
        ? "Waiting for support"
        : isClosed
          ? "Conversation archived"
          : "Start the conversation";

  const description = isLoading
    ? "Fetching your message history."
    : isAdmin && !hasConversation
      ? "New user conversations will appear here once they are assigned to you."
      : isWaiting
        ? "Your message history will appear here once an agent responds."
        : isClosed
          ? "You can review the thread, but sending is disabled."
          : "Send your first message to begin.";

  return (
    <div className="flex h-full min-h-[420px] flex-col items-center justify-center rounded-xl border border-dashed border-gray-300 bg-gray-50 px-6 text-center dark:border-gray-700 dark:bg-gray-950">
      <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-white text-primary shadow-sm dark:bg-gray-900">
        <span className="text-2xl">💬</span>
      </div>
      <h3 className="text-lg font-medium text-gray-900 dark:text-white">
        {title}
      </h3>
      <p className="mt-2 max-w-md text-sm leading-6 text-gray-600 dark:text-gray-400">
        {description}
      </p>
    </div>
  );
}

function InfoBanner({
  title,
  description,
  value,
  action,
}: Readonly<{
  title: string;
  description: string;
  value?: string;
  action?: React.ReactNode;
}>): ReactElement {
  return (
    <div className="rounded-xl border border-gray-200 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-950">
      <p className="font-medium text-gray-900 dark:text-white">{title}</p>
      <p className="mt-1 text-sm text-gray-600 dark:text-gray-400">
        {description}
      </p>
      {value && (
        <p className="mt-2 text-sm font-medium text-primary">{value}</p>
      )}
      {action}
    </div>
  );
}

function SideCard({
  title,
  children,
}: Readonly<{
  title: string;
  children: React.ReactNode;
}>): ReactElement {
  return (
    <div className="rounded-2xl border border-gray-200 bg-white p-5 shadow-sm dark:border-gray-800 dark:bg-gray-900">
      <h2 className="text-lg font-medium text-gray-900 dark:text-white">
        {title}
      </h2>
      <div className="mt-4 space-y-3">{children}</div>
    </div>
  );
}

function DetailRow({
  label,
  value,
}: Readonly<{
  label: string;
  value: string;
}>): ReactElement {
  return (
    <div className="flex items-center justify-between gap-4 text-sm">
      <span className="text-gray-500 dark:text-gray-400">{label}</span>
      <span className="font-medium text-gray-900 dark:text-white">{value}</span>
    </div>
  );
}

function ChatMessageItem({
  message,
  currentSenderName,
  currentSenderType,
}: Readonly<{
  message: RenderableMessage;
  currentSenderName: string;
  currentSenderType: ChatSenderType;
}>): ReactElement {
  const isMine =
    message.senderName === currentSenderName &&
    message.senderType === currentSenderType;

  return (
    <li className={`flex ${isMine ? "justify-end" : "justify-start"}`}>
      <div className="max-w-[82%]">
        <div
          className={`mb-1 flex items-center gap-2 text-xs ${
            isMine ? "justify-end" : "justify-start"
          } text-gray-500 dark:text-gray-400`}
        >
          <span>{message.senderName}</span>
          <span className="rounded-full bg-gray-100 px-2 py-0.5 dark:bg-gray-800">
            {message.senderType}
          </span>
          {message.createdAt && <span>{formatTime(message.createdAt)}</span>}
        </div>
        <div
          className={`rounded-2xl px-4 py-3 ${
            isMine
              ? "bg-primary text-white"
              : "border border-gray-200 bg-white text-gray-900 dark:border-gray-800 dark:bg-gray-950 dark:text-white"
          }`}
        >
          <p className="whitespace-pre-wrap break-words leading-6">
            {message.content}
          </p>
        </div>
      </div>
    </li>
  );
}
