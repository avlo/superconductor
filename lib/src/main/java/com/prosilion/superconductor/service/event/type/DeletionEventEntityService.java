package com.prosilion.superconductor.service.event.type;

import com.prosilion.superconductor.entity.join.deletion.DeletionEventEntity;
import com.prosilion.superconductor.entity.join.deletion.DeletionEventEntityRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DeletionEventEntityService {
  private final DeletionEventEntityRepository repo;

  @Autowired
  public DeletionEventEntityService(@NonNull DeletionEventEntityRepository repo) {
    this.repo = repo;
  }

  public List<DeletionEventEntity> findAll() {
    return repo.findAll();
  }

  protected void addDeletionEvent(@NonNull Long deletedEventId) {
    log.debug("eventId [{}] added to deletion table", deletedEventId);
    repo.save(new DeletionEventEntity(deletedEventId));
  }

  public Optional<Long> getDeletionEvent(@NonNull Long id) {
    return repo.findById(id).map(DeletionEventEntity::getId);
  }

//  more efficient mechanism than below exists in RedisCache
//  public boolean isDeletedEvent(@NonNull Long eventId) {
//    return repo.getDeletionEventEntityByEventId(eventId).isEmpty();
//  }
}
