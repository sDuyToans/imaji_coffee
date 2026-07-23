import { apiSlice } from "@/api/jwt/apiSlice.ts";

export type ChatConversationStatus = "WAITING" | "OPEN" | "PENDING" | "CLOSED";

export type ChatSenderType = "USER" | "ADMIN";

export interface ChatConversationDto {
  id: number;
  customerId?: number;
  assignedAdminId?: number | null;
  status: ChatConversationStatus | string;
  queuePosition?: number | null;
  waitMessage?: string | null;
  messageCount?: number | null;
  unreadCount?: number | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface ChatMessageDto {
  id?: number;
  conversationId?: number;
  content: string;
  senderName: string;
  senderType: ChatSenderType;
  senderId?: number;
  createdAt?: string;
}

export interface ChatMessagePageDto {
  content: ChatMessageDto[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
}

export interface SendChatMessageRequest {
  content: string;
  senderName: string;
  senderType: ChatSenderType;
}

export interface ChatAdminNotificationDto {
  conversationId: number;
  messageId?: number;
  notificationType?: "USER_MESSAGE" | "ADMIN_MESSAGE" | string;
  senderId?: number;
  senderName?: string;
  senderType?: ChatSenderType;
  messagePreview?: string;
  createdAt?: string;
}

export const chatApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getMyCurrentConversation: builder.query<ChatConversationDto, void>({
      query: () => "/chat/me/current",
    }),
    createMyCurrentConversation: builder.mutation<ChatConversationDto, void>({
      query: () => ({
        url: "/chat/me/current",
        method: "POST",
      }),
    }),
    createConversationByCustomerId: builder.mutation<
      ChatConversationDto,
      number
    >({
      query: (customerId) => ({
        url: `/chat/customer/${customerId}`,
        method: "POST",
      }),
    }),
    getConversationMessages: builder.query<ChatMessageDto[], number>({
      query: (conversationId) => `/chat/${conversationId}/messages`,
    }),
    getConversationMessagesPage: builder.query<
      ChatMessagePageDto,
      { conversationId: number; page?: number; size?: number }
    >({
      query: ({ conversationId, page = 0, size = 50 }) =>
        `/chat/${conversationId}/messages/page?page=${page}&size=${size}`,
    }),
    sendConversationMessage: builder.mutation<
      ChatMessageDto,
      { conversationId: number; payload: SendChatMessageRequest }
    >({
      query: ({ conversationId, payload }) => ({
        url: `/chat/${conversationId}/messages`,
        method: "POST",
        body: payload,
      }),
    }),
    markConversationAsRead: builder.mutation<ChatConversationDto, number>({
      query: (conversationId) => ({
        url: `/chat/${conversationId}/read`,
        method: "POST",
      }),
    }),
    getAdminConversations: builder.query<ChatConversationDto[], number>({
      query: (adminId) => `/chat/admin/${adminId}`,
    }),
    reassignConversation: builder.mutation<
      ChatConversationDto,
      { conversationId: number; targetAdminId: number }
    >({
      query: ({ conversationId, targetAdminId }) => ({
        url: `/chat/${conversationId}/reassign/${targetAdminId}`,
        method: "PUT",
      }),
    }),
    closeConversation: builder.mutation<ChatConversationDto, number>({
      query: (conversationId) => ({
        url: `/chat/${conversationId}/close`,
        method: "PUT",
      }),
    }),
  }),
});

export const {
  useLazyGetMyCurrentConversationQuery,
  useCreateMyCurrentConversationMutation,
  useCreateConversationByCustomerIdMutation,
  useLazyGetConversationMessagesQuery,
  useLazyGetConversationMessagesPageQuery,
  useSendConversationMessageMutation,
  useMarkConversationAsReadMutation,
  useLazyGetAdminConversationsQuery,
  useReassignConversationMutation,
  useCloseConversationMutation,
} = chatApi;
