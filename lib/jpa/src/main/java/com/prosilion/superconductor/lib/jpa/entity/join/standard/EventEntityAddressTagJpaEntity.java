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
@Table(name = "event-address_tag-join")
public class EventEntityAddressTagJpaEntity extends EventEntityAbstractJpaEntity {
  private Long addressTagId;

  public EventEntityAddressTagJpaEntity(Long eventId, Long addressTagId) {
    super.setEventId(eventId);
    this.addressTagId = addressTagId;
  }
}
