package vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fsoftacademy.api.infrastructure.persistence.jpa.entity.ChatMessageCitationJpaEntity;

public interface ChatMessageCitationJpaRepository extends JpaRepository<ChatMessageCitationJpaEntity, ChatMessageCitationJpaEntity.Id> {
  List<ChatMessageCitationJpaEntity> findAllByMessageIdInOrderByMessageIdAscCitationNumberAsc(List<UUID> messageIds);
  void deleteByMessageId(UUID messageId);
}
