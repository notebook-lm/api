package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ChatMessageJpaEntity;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageJpaEntity, UUID> {
    List<ChatMessageJpaEntity> findAllByConversationIdOrderByCreatedAtAscIdAsc(UUID conversationId);

    Page<ChatMessageJpaEntity> findByConversationId(UUID conversationId, Pageable pageable);
}
