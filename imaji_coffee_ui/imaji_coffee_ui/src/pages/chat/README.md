# Chat system notes

This page is the live support chat entry point.

## Current flow

1. The page loads the current conversation with REST.
2. The page loads messages for the selected conversation.
3. WebSocket updates append new messages and notifications in real time.
4. When the tab regains focus, the active conversation refreshes once to recover from missed events.

## Backend routes

- `GET /api/v1/chat/me/current`
- `GET /api/v1/chat/{conversationId}/messages`
- `GET /api/v1/chat/{conversationId}/messages/page?page=0&size=50`
- `POST /api/v1/chat/{conversationId}/read`
- `POST /api/v1/chat/me/current`
- `PUT /api/v1/chat/{conversationId}/close`
- `PUT /api/v1/chat/{conversationId}/reassign/{targetAdminId}`

## WebSocket routes

- Client send: `/api/v1/app/chat.sendMessage/{conversationId}`
- Message stream: `/user/queue/chat/messages/{conversationId}`
- User/admin notifications: `/user/queue/chat/notifications`
- Admin broadcast notifications: `/api/v1/topic/chat/admin/notifications`

## Notes

- Message delivery uses `convertAndSendToUser(...)` with destinations under `/queue/...`.
- The service validates message size and ownership before saving.
- Pagination exists for history loading.
- Unread counts are computed on the backend from persisted last-read markers.
