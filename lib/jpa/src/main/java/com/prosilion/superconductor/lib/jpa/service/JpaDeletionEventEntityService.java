package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.superconductor.lib.jpa.dto.deletion.DeletionEventEntityJpaIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntity;
import com.prosilion.superconductor.lib.jpa.repository.deletion.DeletionEventEntityRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JpaDeletionEventEntityService {
  private final DeletionEventEntityRepository repo;

  @Autowired
  public JpaDeletionEventEntityService(@NonNull DeletionEventEntityRepository repo) {
    this.repo = repo;
  }

  //    TODO: fix missing generic  
  public List<DeletionEventEntityJpaIF> findAll() {
//    List<DeletionEventEntityJpaIF> all = repo.findAll();
//    List<DeletionEventEntityJpaIF> deletionEventEntityJpaIFS = Collections.unmodifiableList(all);
//    return deletionEventEntityJpaIFS;
    List<DeletionEventEntity> all = repo.findAll();
    return Collections.unmodifiableList(all);
  }

  public void addDeletionEvent(@NonNull Long deletedEventId) {
    log.debug("eventId [{}] added to deletion table", deletedEventId);
    repo.save(new DeletionEventEntity(deletedEventId));
  }

  public Optional<Long> getDeletionEvent(@NonNull Long id) {
    return repo.findById(id).map(DeletionEventEntityJpaIF::getId);
  }

//  more efficient mechanism than below exists in RedisCache
//  public boolean isDeletedEvent(@NonNull Long eventId) {
//    return repo.getDeletionEventEntityByEventId(eventId).isEmpty();
//  }
}
