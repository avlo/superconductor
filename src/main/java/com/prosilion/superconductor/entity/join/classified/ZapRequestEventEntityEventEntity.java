package com.prosilion.superconductor.entity.join.classified;

import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "zaprequest_event-event-join")
// TODO: refactor join classes
public class ZapRequestEventEntityEventEntity extends EventEntityGenericTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long zapRequestEventId;

  public ZapRequestEventEntityEventEntity(Long eventId, Long zapRequestEventId) {
    this.eventId = eventId;
    this.zapRequestEventId = zapRequestEventId;
  }

  @Override
  public Long getLookupId() {
    return zapRequestEventId;
  }
}