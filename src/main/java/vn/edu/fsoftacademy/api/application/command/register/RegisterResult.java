package vn.edu.fsoftacademy.api.application.command.register;

import java.util.UUID;

/** Identifies the account created by the registration command. */
public record RegisterResult(UUID id, String email, String displayName) {}
