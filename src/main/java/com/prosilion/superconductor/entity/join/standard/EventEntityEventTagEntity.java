package com.prosilion.superconductor.entity.join.standard;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-event_tag-join")
public class EventEntityEventTagEntity extends EventEntityStandardTagEntity {
  private Long eventTagId;

  public <T extends EventEntityStandardTagEntity> EventEntityEventTagEntity(Long eventId, Long eventTagId) {
    super.setEventId(eventId);
    this.eventTagId = eventTagId;
  }

  @Override
  public Long getLookupId() {
    return eventTagId;
  }
}