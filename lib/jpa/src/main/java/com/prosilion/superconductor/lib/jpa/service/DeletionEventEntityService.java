package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntityIF;
import com.prosilion.superconductor.lib.jpa.repository.deletion.DeletionEventEntityRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DeletionEventEntityService {
  private final DeletionEventEntityRepository repo;

  public DeletionEventEntityService(@NonNull DeletionEventEntityRepository repo) {
    this.repo = repo;
  }

  public List<DeletionEventEntityIF> findAll() {
    List<DeletionEventEntity> all = repo.findAll();
    return Collections.unmodifiableList(all);
  }

  protected void addDeletionEvent(@NonNull EventEntityIF deletedEventId) {
    log.debug("eventId [{}] added to deletion table", deletedEventId);
    repo.save(new DeletionEventEntity(deletedEventId.getUid()));
  }

  public Optional<Long> getDeletionEvent(@NonNull Long id) {
    return repo.findById(id).map(DeletionEventEntityIF::getId);
  }
}
