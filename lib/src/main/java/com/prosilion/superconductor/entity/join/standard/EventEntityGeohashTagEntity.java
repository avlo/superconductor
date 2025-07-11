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
@RedisHash("event-geohash_tag-join")
public class EventEntityGeohashTagEntity extends EventEntityAbstractEntity {
  private Long geohashTagId;

  public EventEntityGeohashTagEntity(Long eventId, Long geohashTagId) {
    super.setEventId(eventId);
    this.geohashTagId = geohashTagId;
  }
}
