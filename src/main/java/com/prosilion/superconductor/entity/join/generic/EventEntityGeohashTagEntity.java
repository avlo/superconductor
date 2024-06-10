package com.prosilion.superconductor.entity.join.generic;

import com.prosilion.superconductor.entity.join.EventEntityAbstractTagEntity;
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
public class EventEntityGeohashTagEntity extends EventEntityAbstractTagEntity {
  private Long geohashTagId;

  public <T extends EventEntityAbstractTagEntity> EventEntityGeohashTagEntity(Long eventId, Long geohashTagId) {
    super.setEventId(eventId);
    this.geohashTagId = geohashTagId;
  }
  @Override
  public Long getLookupId() {
    return geohashTagId;
  }
}