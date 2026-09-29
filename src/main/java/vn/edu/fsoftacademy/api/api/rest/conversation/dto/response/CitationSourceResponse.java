package vn.edu.fsoftacademy.api.api.rest.conversation.dto.response;

import java.util.UUID;

public record CitationSourceResponse(int citationNumber, UUID documentId, String filename, int chunkIndex, String excerpt) {}
