package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber-filter_event-join")
public class SubscriberFilterEvent implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long filterId;
  private String eventId;

  public SubscriberFilterEvent(Long filterId, String eventId) {
    this.filterId = filterId;
    this.eventId = eventId;
  }
}