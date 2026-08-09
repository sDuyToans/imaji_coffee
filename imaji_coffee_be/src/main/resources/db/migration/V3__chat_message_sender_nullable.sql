-- Allow system-generated chat messages (e.g. admin-offline auto-reply) that are
-- not sent by a real user account.
ALTER TABLE chat_message MODIFY COLUMN sender_id BIGINT NULL;
