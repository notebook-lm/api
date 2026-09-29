package vn.edu.fsoftacademy.api.api.rest.conversation.dto.response;
import java.util.List;
public record ConversationPageResponse(List<ConversationResponse> items,int page,int size,long totalItems,int totalPages,boolean hasNext,boolean hasPrevious) {}
