ALTER TABLE chat_messages DROP CONSTRAINT IF EXISTS chat_messages_status_check;
ALTER TABLE chat_messages
    ADD CONSTRAINT chat_messages_status_check
    CHECK (status IN ('COMPLETED', 'STREAMING', 'CANCELLED', 'FAILED'));
