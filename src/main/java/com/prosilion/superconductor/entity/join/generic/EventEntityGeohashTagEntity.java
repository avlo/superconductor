package com.prosilion.superconductor.entity.join.generic;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Entity
@Table(name = "event-geohash_tag-join")
public class EventEntityGeohashTagEntity extends EventEntityGenericTagEntity {
  private Long geohashTagId;

  public <T extends EventEntityGenericTagEntity> EventEntityGeohashTagEntity(Long eventId, Long geohashTagId) {
    this.eventId = eventId;
    this.geohashTagId = geohashTagId;
  }
}