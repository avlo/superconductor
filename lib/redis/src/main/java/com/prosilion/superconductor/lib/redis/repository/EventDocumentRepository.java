package com.prosilion.superconductor.lib.redis.repository;

import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.redis.om.spring.repository.RedisDocumentRepository;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface EventDocumentRepository extends RedisDocumentRepository<EventDocument, String> {
  Optional<EventDocument> findByEventIdString(@NonNull String eventIdString);
}
