ALTER TABLE outbox_events
    ADD COLUMN claimed_until TIMESTAMPTZ;

CREATE INDEX idx_outbox_events_claimable
    ON outbox_events (next_attempt_at, claimed_until, created_at)
    WHERE published_at IS NULL;
