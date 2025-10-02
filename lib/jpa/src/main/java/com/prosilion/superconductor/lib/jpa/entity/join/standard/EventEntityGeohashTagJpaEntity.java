package com.prosilion.superconductor.lib.jpa.entity.join.standard;

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
@Table(name = "event-geohash_tag-join")
public class EventEntityGeohashTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long geohashTagId;

  public EventEntityGeohashTagJpaEntity(Long eventId, Long geohashTagId) {
    super.setEventId(eventId);
    this.geohashTagId = geohashTagId;
  }
}
