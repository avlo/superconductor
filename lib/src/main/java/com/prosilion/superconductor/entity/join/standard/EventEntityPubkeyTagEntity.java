package com.prosilion.superconductor.entity.join.standard;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.superconductor.entity.join.EventEntityAbstractEntity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("event-pubkey_tag-join")
public class EventEntityPubkeyTagEntity extends EventEntityAbstractEntity {
  private Long pubkeyId;

  public EventEntityPubkeyTagEntity(Long eventId, Long pubkeyId) {
    super.setEventId(eventId);
    this.pubkeyId = pubkeyId;
  }
}
