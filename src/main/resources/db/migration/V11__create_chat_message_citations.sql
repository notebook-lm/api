CREATE TABLE chat_message_citations (
    message_id UUID NOT NULL REFERENCES chat_messages(id) ON DELETE CASCADE,
    citation_number INTEGER NOT NULL CHECK (citation_number > 0),
    document_id UUID NOT NULL,
    filename VARCHAR(1024) NOT NULL,
    chunk_index INTEGER NOT NULL CHECK (chunk_index >= 0),
    excerpt TEXT NOT NULL,
    PRIMARY KEY (message_id, citation_number)
);

CREATE INDEX idx_chat_message_citations_message_order
    ON chat_message_citations(message_id, citation_number);
