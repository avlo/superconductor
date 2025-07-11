package com.prosilion.superconductor.base.dto.deletion;

import com.prosilion.superconductor.base.entity.join.deletion.DeletionEventEntity;

public class DeletionEventDto {
  private final Long eventId;

  public DeletionEventDto(Long eventId) {
    this.eventId = eventId;
  }

  public DeletionEventEntity convertDtoToEntity() {
    return new DeletionEventEntity(this.eventId);
  }
}
