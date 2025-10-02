package com.prosilion.superconductor.lib.jpa.entity.join.standard;

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
@Table(name = "event-identifier_tag-join")
public class EventEntityIdentifierTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long identifierTagId;

  public EventEntityIdentifierTagJpaEntity(Long eventId, Long identifierTagId) {
    super.setEventId(eventId);
    this.identifierTagId = identifierTagId;
  }
}
