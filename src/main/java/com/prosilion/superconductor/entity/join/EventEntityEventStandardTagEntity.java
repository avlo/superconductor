package com.prosilion.superconductor.entity.join;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-event_standard_tag-join")
public class EventEntityEventStandardTagEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long eventId;
  private Long eventStandardTagId;

  public EventEntityEventStandardTagEntity(Long eventId, Long eventStandardTagId) {
    this.eventId = eventId;
    this.eventStandardTagId = eventStandardTagId;
  }
}