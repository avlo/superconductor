package com.prosilion.superconductor.lib.jpa.entity.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "event-event_tag-join")
public class EventEntityEventTagJpaEntity extends EventEntityAbstractJpaEntity {
  public EventEntityEventTagJpaEntity(Long eventId, Long tagId) {
    super(eventId, tagId);
  }
}
