package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.repository.deletion.DeletionEventJpaEntityRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DeletionEventJpaEntityService {
  private final DeletionEventJpaEntityRepository repo;

  public DeletionEventJpaEntityService(@NonNull DeletionEventJpaEntityRepository repo) {
    this.repo = repo;
  }

  public List<DeletionEventJpaEntityIF> findAll() {
    List<DeletionEventJpaEntity> all = repo.findAll();
    return Collections.unmodifiableList(all);
  }

  protected void addDeletionEvent(@NonNull EventJpaEntityIF deletedEventId) {
    log.debug("eventId [{}] added to deletion table", deletedEventId);
    repo.save(new DeletionEventJpaEntity(deletedEventId.getUid()));
  }

  public Optional<Long> getDeletionEvent(@NonNull Long id) {
    return repo.findById(id).map(DeletionEventJpaEntityIF::getId);
  }
}
