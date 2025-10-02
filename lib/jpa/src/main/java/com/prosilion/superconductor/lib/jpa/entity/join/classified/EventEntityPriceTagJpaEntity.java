package com.prosilion.superconductor.lib.jpa.entity.join.classified;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
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
public class EventEntityPriceTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long priceTagId;

  public EventEntityPriceTagJpaEntity(Long eventId, Long priceTagId) {
    super.setEventId(eventId);
    this.priceTagId = priceTagId;
  }
}
