package com.prosilion.superconductor.base.entity.join.standard;

import com.prosilion.superconductor.base.entity.join.EventEntityAbstractEntity;
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
public class EventEntityGeohashTagEntity extends EventEntityAbstractEntity {
  private Long geohashTagId;

  public EventEntityGeohashTagEntity(Long eventId, Long geohashTagId) {
    super.setEventId(eventId);
    this.geohashTagId = geohashTagId;
  }
}
