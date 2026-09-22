package vn.edu.fsoftacademy.api.application.query.currentuser;

import java.util.UUID;

/** Identifies the authenticated user whose profile should be read. */
public record GetCurrentUserQuery(UUID userId) {}
