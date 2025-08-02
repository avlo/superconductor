package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.superconductor.lib.jpa.dto.deletion.DeletionEventEntityJpaIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntity;
import com.prosilion.superconductor.lib.jpa.repository.deletion.DeletionEventEntityRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class JpaDeletionEventEntityService {
  private final DeletionEventEntityRepository repo;

  public JpaDeletionEventEntityService(@NonNull DeletionEventEntityRepository repo) {
    this.repo = repo;
  }

  public List<DeletionEventEntityJpaIF> findAll() {
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
}
