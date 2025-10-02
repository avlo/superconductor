package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntity;
import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.repository.DeletionEventNosqlEntityRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DeletionEventNoSqlEntityService {
  private final DeletionEventNosqlEntityRepository repo;

  public DeletionEventNoSqlEntityService(@NonNull DeletionEventNosqlEntityRepository deletionEventNosqlEntityRepository) {
    this.repo = deletionEventNosqlEntityRepository;
  }

  public Optional<DeletionEventNosqlEntityIF> findByEventIdString(@NonNull String eventIdString) {
    return repo.findByNosqlEntityEventId(eventIdString);
  }

  public List<DeletionEventNosqlEntityIF> getAll() {
    return repo.findAllNosqlEntities();
  }

  protected void addDeletionEvent(@NonNull EventNosqlEntityIF event) {
    log.debug("{}} deleteEventEntity: {}", getClass().getSimpleName(), event);
    repo.save(DeletionEventNosqlEntity.of(event.getEventId()));
  }
}
