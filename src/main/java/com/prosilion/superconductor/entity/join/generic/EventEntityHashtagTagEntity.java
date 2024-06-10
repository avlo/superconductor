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
@Table(name = "event-hashtag_tag-join")
public class EventEntityHashtagTagEntity extends EventEntityAbstractTagEntity {
  private Long hashTagId;

  public <T extends EventEntityAbstractTagEntity> EventEntityHashtagTagEntity(Long eventId, Long hashTagId) {
    super.setEventId(eventId);
    this.hashTagId = hashTagId;
  }
  @Override
  public Long getLookupId() {
    return hashTagId;
  }
}