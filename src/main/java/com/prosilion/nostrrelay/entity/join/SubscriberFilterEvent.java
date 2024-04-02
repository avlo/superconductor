package com.prosilion.nostrrelay.entity.join;

import com.prosilion.nostrrelay.entity.Subscriber;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
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