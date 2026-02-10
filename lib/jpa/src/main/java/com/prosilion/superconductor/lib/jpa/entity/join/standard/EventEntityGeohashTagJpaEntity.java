package com.prosilion.superconductor.lib.jpa.entity.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "event-geohash_tag-join")
public class EventEntityGeohashTagJpaEntity extends EventEntityAbstractJpaEntity {
  public EventEntityGeohashTagJpaEntity(Long eventId, Long tagId) {
    super(eventId, tagId);
  }
}
