package com.prosilion.superconductor.entity.join.generic;

import com.prosilion.superconductor.entity.join.EventEntityAbstractEntity;
import org.springframework.data.redis.core.RedisHash;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("event-generic_tag-join")
public class EventEntityGenericTagEntity extends EventEntityAbstractEntity {
  private Long genericTagId;

  public EventEntityGenericTagEntity(Long eventId, Long genericTagId) {
    super.setEventId(eventId);
    this.genericTagId = genericTagId;
  }
}
