package com.prosilion.superconductor.entity.join.standard;

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
public class EventEntityPubkeyTagEntity extends EventEntityAbstractTagEntity {
  private Long pubkeyId;

  public <T extends EventEntityAbstractTagEntity> EventEntityPubkeyTagEntity(Long eventId, Long pubkeyId) {
    super.setEventId(eventId);
    this.pubkeyId = pubkeyId;
  }

  @Override
  public Long getLookupId() {
    return pubkeyId;
  }
}