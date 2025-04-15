package com.prosilion.superconductor.entity.join.standard;

import com.prosilion.superconductor.entity.join.EventEntityAbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-relays_tag-join")
public class EventEntityRelaysTagEntity extends EventEntityAbstractEntity {
  private Long relaysId;

  public EventEntityRelaysTagEntity(Long eventId, Long relaysId) {
    super.setEventId(eventId);
    this.relaysId = relaysId;
  }
}
