package com.prosilion.superconductor.entity.join.standard;

import com.prosilion.superconductor.entity.join.EventEntityAbstractTagEntity;
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
public class EventEntityEventTagEntity extends EventEntityAbstractTagEntity {
  private Long eventTagId;

  public <T extends EventEntityAbstractTagEntity> EventEntityEventTagEntity(Long eventId, Long eventTagId) {
    super.setEventId(eventId);
    this.eventTagId = eventTagId;
  }
}