package com.prosilion.superconductor.lib.jpa.dto.deletion;

import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntityIF;

public class DeletionEventDto {
  private final Long eventId;

  public DeletionEventDto(Long eventId) {
    this.eventId = eventId;
  }

  public DeletionEventEntityIF convertDtoToEntity() {
    return new DeletionEventEntity(eventId);
  }
}
