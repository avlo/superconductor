package com.prosilion.superconductor.lib.jpa.dto.deletion;

import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import org.springframework.lang.NonNull;

public class DeletionEventDto {
  private final Long eventId;

  public DeletionEventDto(@NonNull Long eventId) {
    this.eventId = eventId;
  }

  public DeletionEventJpaEntityIF convertDtoToEntity() {
    return new DeletionEventJpaEntity(eventId);
  }
}
