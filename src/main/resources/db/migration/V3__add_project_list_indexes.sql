CREATE INDEX idx_projects_owner_updated_at ON projects(owner_id, updated_at DESC);
CREATE INDEX idx_projects_owner_title ON projects(owner_id, title);
