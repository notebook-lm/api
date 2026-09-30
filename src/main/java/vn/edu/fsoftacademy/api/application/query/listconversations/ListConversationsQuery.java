package vn.edu.fsoftacademy.api.application.query.listconversations;
public record ListConversationsQuery(String query, int page, int size) { public ListConversationsQuery { query = query == null || query.isBlank() ? null : query.strip(); } }
