ALTER TABLE project_documents
    ADD COLUMN processing_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED'
    CHECK (processing_status IN ('PENDING', 'PROCESSING', 'COMPLETED'));
