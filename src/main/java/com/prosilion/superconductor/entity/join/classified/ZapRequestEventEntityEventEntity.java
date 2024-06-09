package com.prosilion.superconductor.entity.join.classified;

import jakarta.persistence.*;
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
public class ZapRequestEventEntityEventEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long zapRequestEventId;

  public ZapRequestEventEntityEventEntity(Long eventId, Long zapRequestEventId) {
    this.eventId = eventId;
    this.zapRequestEventId = zapRequestEventId;
  }
}