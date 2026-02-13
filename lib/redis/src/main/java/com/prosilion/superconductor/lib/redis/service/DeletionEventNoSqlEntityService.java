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

  public List<DeletionEventNosqlEntityIF> getAll() {
    return repo.findAllNosqlEntities();
  }

  protected void addDeletionEvent(@NonNull EventNosqlEntityIF event) {
    log.debug("added deleteEventEntity type EventNosqlEntityIF: {}", event);
    repo.save(DeletionEventNosqlEntity.of(event.getId()));
  }

  public Optional<DeletionEventNosqlEntityIF> getDeletionEvent(@NonNull String eventIdString) {
    return repo.findByNosqlEntityEventId(eventIdString);
  }
}
