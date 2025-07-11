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
@RedisHash("event-address_tag-join")
public class EventEntityAddressTagEntity extends EventEntityAbstractEntity {
  private Long addressTagId;

  public EventEntityAddressTagEntity(Long eventId, Long addressTagId) {
    super.setEventId(eventId);
    this.addressTagId = addressTagId;
  }
}
