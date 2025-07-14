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
@Table(name = "event-identifier_tag-join")
public class EventEntityIdentifierTagEntity extends EventEntityAbstractEntity {
  private Long identifierTagId;

  public EventEntityIdentifierTagEntity(Long eventId, Long identifierTagId) {
    super.setEventId(eventId);
    this.identifierTagId = identifierTagId;
  }
}
