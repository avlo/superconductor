package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.superconductor.lib.redis.document.DeletionEventDocument;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentIF;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
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

  public Optional<DeletionEventDocumentIF> findByEventIdString(@NonNull String eventIdString) {
    return repo.findByDocumentEventId(eventIdString);
  }

  public List<DeletionEventDocumentIF> getAll() {
    return repo.findAllDocuments();
  }

  protected void addDeletionEvent(@NonNull EventDocumentIF event) {
    log.debug("DeletionEventDocumentService deleteEventEntity: {}", event);
    repo.save(DeletionEventDocument.of(event.getEventId()));
  }
}
