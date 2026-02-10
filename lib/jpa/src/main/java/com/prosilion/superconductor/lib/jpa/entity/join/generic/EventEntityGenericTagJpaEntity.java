package com.prosilion.superconductor.lib.jpa.entity.join.generic;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "event-generic_tag-join")
public class EventEntityGenericTagJpaEntity extends EventEntityAbstractJpaEntity {
  public EventEntityGenericTagJpaEntity(Long eventId, Long tagId) {
    super(eventId, tagId);
  }
}
