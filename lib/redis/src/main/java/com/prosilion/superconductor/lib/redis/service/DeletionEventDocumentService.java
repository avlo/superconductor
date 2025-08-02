package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.superconductor.lib.redis.document.DeletionEventDocument;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentRedisIF;
import com.prosilion.superconductor.lib.redis.repository.DeletionEventDocumentRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DeletionEventDocumentService {
  private final DeletionEventDocumentRepository repo;

  public DeletionEventDocumentService(@NonNull DeletionEventDocumentRepository deletionEventDocumentRepository) {
    this.repo = deletionEventDocumentRepository;
  }

  public Optional<DeletionEventDocumentRedisIF> findByEventIdString(@NonNull String eventIdString) {
    Optional<DeletionEventDocumentRedisIF> byEventIdCustom = repo.findByDocumentEventId(eventIdString);
    return byEventIdCustom;
  }

  public List<DeletionEventDocumentRedisIF> getAll() {
    return repo
        .findAllDocuments().stream()
        .toList();
  }

  protected void deleteEventEntity(@NonNull String eventId) {
    Optional<DeletionEventDocument> byEventId = repo.findByEventId(eventId);
    Optional<DeletionEventDocument> deletionEventDocument = byEventId.map(repo::save).map(DeletionEventDocument::getEventId).map(DeletionEventDocument::of);
    log.debug("DeletionEventDocumentService deletionEventDocument: {}", deletionEventDocument);
  }
}
