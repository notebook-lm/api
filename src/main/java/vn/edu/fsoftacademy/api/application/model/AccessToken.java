package vn.edu.fsoftacademy.api.application.model;

public record AccessToken(String value, String tokenType, long expiresIn) {}
