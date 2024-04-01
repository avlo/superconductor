package com.prosilion.nostrrelay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Getter
@NoArgsConstructor
@Entity
public class SubscriberFilterEvent implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long subscriberId;
  private String eventId;

  public SubscriberFilterEvent(Subscriber subscriber, String eventId) {
    this.subscriberId = subscriber.getId();
    this.eventId = eventId;
  }
}