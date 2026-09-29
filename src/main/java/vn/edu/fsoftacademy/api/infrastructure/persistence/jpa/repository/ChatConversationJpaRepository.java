package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ChatConversationJpaEntity;

public interface ChatConversationJpaRepository
        extends JpaRepository<ChatConversationJpaEntity, UUID>, JpaSpecificationExecutor<ChatConversationJpaEntity> {
    Optional<ChatConversationJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
}
