ALTER TABLE project_documents
    DROP CONSTRAINT project_documents_processing_status_check;

ALTER TABLE project_documents
    ADD CONSTRAINT project_documents_processing_status_check
    CHECK (processing_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'));
