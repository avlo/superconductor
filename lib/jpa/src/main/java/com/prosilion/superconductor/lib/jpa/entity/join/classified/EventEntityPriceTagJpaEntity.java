package com.prosilion.superconductor.lib.jpa.entity.join.classified;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "event-price_tag-join")
public class EventEntityPriceTagJpaEntity extends EventEntityAbstractJpaEntity {
  public EventEntityPriceTagJpaEntity(Long eventId, Long tagId) {
    super(eventId, tagId);
  }
}
