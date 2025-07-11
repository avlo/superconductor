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
@RedisHash("event-reference_tag-join")
public class EventEntityReferenceTagEntity extends EventEntityAbstractEntity {
  private Long referenceTagId;

  public EventEntityReferenceTagEntity(Long eventId, Long referenceTagId) {
    super.setEventId(eventId);
    this.referenceTagId = referenceTagId;
  }
}
