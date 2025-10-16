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
@Table(name = "event-external_identity_tag-join")
public class EventEntityExternalIdentityTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long externalIdentityTag;

  public EventEntityExternalIdentityTagJpaEntity(Long eventId, Long externalIdentityTag) {
    super.setEventId(eventId);
    this.externalIdentityTag = externalIdentityTag;
  }
}
