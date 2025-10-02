package com.prosilion.superconductor.lib.jpa.entity.join.generic;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-generic_tag-join")
public class EventEntityGenericTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long genericTagId;

  public EventEntityGenericTagJpaEntity(Long eventId, Long genericTagId) {
    super.setEventId(eventId);
    this.genericTagId = genericTagId;
  }
}
