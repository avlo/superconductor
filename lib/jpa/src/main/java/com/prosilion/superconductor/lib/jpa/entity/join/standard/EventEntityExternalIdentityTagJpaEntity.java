package com.prosilion.superconductor.lib.jpa.entity.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "event-external_identity_tag-join")
public class EventEntityExternalIdentityTagJpaEntity extends EventEntityAbstractJpaEntity {
  public EventEntityExternalIdentityTagJpaEntity(Long eventId, Long tagId) {
    super(eventId, tagId);
  }
}
