package com.prosilion.superconductor.entity.join.classified;

import com.prosilion.superconductor.entity.join.standard.EventEntityAbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-price_tag-join")
public class EventEntityPriceTagEntity extends EventEntityAbstractTagEntity {
  private Long priceTagId;

  public <T extends EventEntityAbstractTagEntity> EventEntityPriceTagEntity(Long eventId, Long priceTagId) {
    super.setEventId(eventId);
    this.priceTagId = priceTagId;
  }

  @Override
  public Long getLookupId() {
    return priceTagId;
  }
}