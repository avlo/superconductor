package com.prosilion.superconductor.service.event.type;

import com.prosilion.superconductor.entity.join.deletion.DeletionEventEntity;
import com.prosilion.superconductor.entity.join.deletion.DeletionEventEntityRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeletionEventService {
  private final DeletionEventEntityRepository join;

  @Autowired
  public DeletionEventService(@NonNull DeletionEventEntityRepository join) {
    this.join = join;
  }
  protected void addHiddenEvent(@NonNull Long savedEventId) {
    join.save(new DeletionEventEntity(savedEventId));
  }
}
