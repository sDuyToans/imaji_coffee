import { apiSlice } from "@/api/jwt/apiSlice.ts";
import {
  AdminAiAskRequest,
  AdminAiAskResponse,
  AdminDashboardSummaryDto,
  AdminSuggestedQuestionsResponse,
} from "@/types";

export const adminAiApi = apiSlice.injectEndpoints({
  endpoints: (builder) => ({
    getAdminAiSummary: builder.query<AdminDashboardSummaryDto, void>({
      query: () => "/admin/ai-insights/summary",
    }),
    getAdminAiSuggestedQuestions: builder.query<
      AdminSuggestedQuestionsResponse,
      void
    >({
      query: () => "/admin/ai-insights/suggested-questions",
    }),
    askAdminAiQuestion: builder.mutation<AdminAiAskResponse, AdminAiAskRequest>(
      {
        query: (body) => ({
          url: "/admin/ai-insights/ask",
          method: "POST",
          body,
        }),
      },
    ),
  }),
});

export const {
  useGetAdminAiSummaryQuery,
  useGetAdminAiSuggestedQuestionsQuery,
  useAskAdminAiQuestionMutation,
} = adminAiApi;
