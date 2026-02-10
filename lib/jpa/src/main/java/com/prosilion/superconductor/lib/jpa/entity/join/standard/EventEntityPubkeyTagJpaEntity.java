package com.prosilion.superconductor.lib.jpa.entity.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "event-pubkey_tag-join")
public class EventEntityPubkeyTagJpaEntity extends EventEntityAbstractJpaEntity {
  public EventEntityPubkeyTagJpaEntity(Long eventId, Long tagId) {
    super(eventId, tagId);
  }
}
