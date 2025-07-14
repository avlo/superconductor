package com.prosilion.superconductor.lib.jpa.entity.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
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
public class EventEntityEventTagEntity extends EventEntityAbstractEntity {
  private Long eventTagId;

  public EventEntityEventTagEntity(Long eventId, Long eventTagId) {
    super.setEventId(eventId);
    this.eventTagId = eventTagId;
  }
}
