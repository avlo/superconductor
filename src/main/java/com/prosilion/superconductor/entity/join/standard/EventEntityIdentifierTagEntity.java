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
@Table(name = "event-identifier_tag-join")
public class EventEntityIdentifierTagEntity extends EventEntityAbstractTagEntity {
  private Long identifierTagId;

  public <T extends EventEntityAbstractTagEntity> EventEntityIdentifierTagEntity(Long eventId, Long identifierTagId) {
    super.setEventId(eventId);
    this.identifierTagId = identifierTagId;
  }
}