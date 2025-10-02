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
@Table(name = "event-hashtag_tag-join")
public class EventEntityHashtagTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long hashTagId;

  public EventEntityHashtagTagJpaEntity(Long eventId, Long hashTagId) {
    super.setEventId(eventId);
    this.hashTagId = hashTagId;
  }
}
