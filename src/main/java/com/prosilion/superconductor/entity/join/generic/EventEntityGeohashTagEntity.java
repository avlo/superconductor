package com.prosilion.superconductor.entity.join.generic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-geohash_tag-join")
public class EventEntityGeohashTagEntity implements EventEntityGenericTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long geohashTagId;

  public <T extends EventEntityGenericTagEntity> EventEntityGeohashTagEntity(Long eventId, Long geohashTagId) {
    this.eventId = eventId;
    this.geohashTagId = geohashTagId;
  }
}