package com.prosilion.superconductor.dto.deletion;

import com.prosilion.superconductor.entity.join.deletion.DeletionEventEntity;

public class DeletionEventDto {
  private final Long eventId;

  public DeletionEventDto(Long eventId) {
    this.eventId = eventId;
  }

  public DeletionEventEntity convertDtoToEntity() {
    return new DeletionEventEntity(this.eventId);
  }
}
