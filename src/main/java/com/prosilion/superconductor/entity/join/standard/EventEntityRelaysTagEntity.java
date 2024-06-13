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
@Table(name = "event-relays_tag-join")
public class EventEntityRelaysTagEntity extends EventEntityAbstractTagEntity {
  private Long relaysId;

  public <T extends EventEntityAbstractTagEntity> EventEntityRelaysTagEntity(Long eventId, Long relaysId) {
    super.setEventId(eventId);
    this.relaysId = relaysId;
  }

// TODO: possibly remove
  @Override
  public Long getLookupId() {
    return relaysId;
  }
}