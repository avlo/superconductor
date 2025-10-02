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
@Table(name = "event-reference_tag-join")
public class EventEntityReferenceTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long referenceTagId;

  public EventEntityReferenceTagJpaEntity(Long eventId, Long referenceTagId) {
    super.setEventId(eventId);
    this.referenceTagId = referenceTagId;
  }
}
