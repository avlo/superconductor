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
@Table(name = "event-pubkey_tag-join")
public class EventEntityPubkeyTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long pubkeyId;

  public EventEntityPubkeyTagJpaEntity(Long eventId, Long pubkeyId) {
    super.setEventId(eventId);
    this.pubkeyId = pubkeyId;
  }
}
