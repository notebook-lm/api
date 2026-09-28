package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.adapter;
import jakarta.persistence.criteria.Predicate;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import vn.edu.fsoftacademy.api.application.query.listconversations.ListConversationsQuery;
import vn.edu.fsoftacademy.api.application.repository.ChatConversationRepository;
import vn.edu.fsoftacademy.api.domain.entity.ChatConversation;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ChatConversationJpaEntity;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository.ChatConversationJpaRepository;
import vn.edu.fsoftacademy.api.shared.pagination.PageResult;
@Repository public class JpaChatConversationAdapter implements ChatConversationRepository {
 private final ChatConversationJpaRepository conversations; public JpaChatConversationAdapter(ChatConversationJpaRepository conversations){this.conversations=conversations;}
 public ChatConversation save(ChatConversation c){conversations.save(toEntity(c));return c;}
 public Optional<ChatConversation> findByIdAndProjectId(UUID id,UUID projectId){return conversations.findByIdAndProjectId(id,projectId).map(this::toDomain);}
 public PageResult<ChatConversation> findPageByProjectId(UUID projectId,ListConversationsQuery q){var r=conversations.findAll(spec(projectId,q),PageRequest.of(q.page(),q.size(),Sort.by(Sort.Order.desc("lastMessageAt"),Sort.Order.desc("createdAt"))));return new PageResult<>(r.getContent().stream().map(this::toDomain).toList(),r.getNumber(),r.getSize(),r.getTotalElements(),r.getTotalPages(),r.hasNext(),r.hasPrevious());}
 public void delete(ChatConversation c){conversations.deleteById(c.getId());}
 private Specification<ChatConversationJpaEntity> spec(UUID projectId,ListConversationsQuery q){return (root,c,b)->{var p=new ArrayList<Predicate>();p.add(b.equal(root.get("projectId"),projectId));if(q.query()!=null)p.add(b.like(b.lower(root.get("title")),"%"+q.query().toLowerCase()+"%"));return b.and(p.toArray(Predicate[]::new));};}
 private ChatConversation toDomain(ChatConversationJpaEntity e){return new ChatConversation(e.getId(),e.getProjectId(),e.getTitle(),e.getLastMessageAt(),e.getCreatedAt(),e.getUpdatedAt());}
 private ChatConversationJpaEntity toEntity(ChatConversation e){return new ChatConversationJpaEntity(e.getId(),e.getProjectId(),e.getTitle(),e.getLastMessageAt(),e.getCreatedAt(),e.getUpdatedAt());}
}
