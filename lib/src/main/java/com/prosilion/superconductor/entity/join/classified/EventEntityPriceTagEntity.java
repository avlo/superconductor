package com.prosilion.superconductor.entity.join.classified;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.prosilion.superconductor.entity.join.EventEntityAbstractEntity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("event-price_tag-join")
public class EventEntityPriceTagEntity extends EventEntityAbstractEntity {
  private Long priceTagId;

  public EventEntityPriceTagEntity(Long eventId, Long priceTagId) {
    super.setEventId(eventId);
    this.priceTagId = priceTagId;
  }
}
