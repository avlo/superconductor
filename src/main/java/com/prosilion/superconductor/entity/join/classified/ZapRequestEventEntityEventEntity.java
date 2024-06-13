package com.prosilion.superconductor.entity.join.classified;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "zaprequest_event-event-join")
public class ZapRequestEventEntityEventEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long zapRequestEventId;

  public ZapRequestEventEntityEventEntity(@NonNull Long eventId, @NonNull Long zapRequestEventId) {
    this.eventId = eventId;
    this.zapRequestEventId = zapRequestEventId;
  }
}