CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(128) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0 CHECK (attempts >= 0),
    next_attempt_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMPTZ,
    last_error TEXT
);
CREATE INDEX idx_outbox_events_pending
    ON outbox_events (next_attempt_at, created_at)
    WHERE published_at IS NULL;
