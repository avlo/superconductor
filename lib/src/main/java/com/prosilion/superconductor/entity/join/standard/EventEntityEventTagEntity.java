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
@RedisHash("event-event_tag-join")
public class EventEntityEventTagEntity extends EventEntityAbstractEntity {
  private Long eventTagId;

  public EventEntityEventTagEntity(Long eventId, Long eventTagId) {
    super.setEventId(eventId);
    this.eventTagId = eventTagId;
  }
}
