package prosilion.superconductor.lib.jpa.dto.deletion;

import prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntity;

public class DeletionEventDto {
  private final Long eventId;

  public DeletionEventDto(Long eventId) {
    this.eventId = eventId;
  }

  public DeletionEventEntity convertDtoToEntity() {
    return new DeletionEventEntity(this.eventId);
  }
}
