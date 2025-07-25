package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.superconductor.base.DeletionEventEntityIF;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntity;
import com.prosilion.superconductor.lib.jpa.repository.deletion.DeletionEventEntityRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeletionEventEntityService {
  private final DeletionEventEntityRepository repo;

  @Autowired
  public DeletionEventEntityService(@NonNull DeletionEventEntityRepository repo) {
    this.repo = repo;
  }

  public List<DeletionEventEntityIF> findAll() {
//    return repo.findAll(DeletionEventEntityIF.class);
    return Collections.unmodifiableList(repo.findAll());
  }

  protected void addDeletionEvent(@NonNull Long deletedEventId) {
    log.debug("eventId [{}] added to deletion table", deletedEventId);
    repo.save(new DeletionEventEntity(deletedEventId));
  }

  public Optional<Long> getDeletionEvent(@NonNull Long id) {
    return repo.findById(id).map(DeletionEventEntityIF::getId);
  }

//  more efficient mechanism than below exists in RedisCache
//  public boolean isDeletedEvent(@NonNull Long eventId) {
//    return repo.getDeletionEventEntityByEventId(eventId).isEmpty();
//  }
}
