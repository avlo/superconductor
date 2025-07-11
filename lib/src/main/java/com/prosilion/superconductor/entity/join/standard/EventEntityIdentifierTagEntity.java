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
@RedisHash("event-identifier_tag-join")
public class EventEntityIdentifierTagEntity extends EventEntityAbstractEntity {
  private Long identifierTagId;

  public EventEntityIdentifierTagEntity(Long eventId, Long identifierTagId) {
    super.setEventId(eventId);
    this.identifierTagId = identifierTagId;
  }
}
