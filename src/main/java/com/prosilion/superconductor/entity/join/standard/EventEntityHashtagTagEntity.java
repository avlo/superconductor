package com.prosilion.superconductor.entity.join.standard;

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

  public EventEntityHashtagTagEntity(Long eventId, Long hashTagId) {
    super.setEventId(eventId);
    this.hashTagId = hashTagId;
  }
}
