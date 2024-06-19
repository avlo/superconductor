package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber-filter_event")
public class SubscriberFilterEvent implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long filterId;
  private String eventIdString;

  public SubscriberFilterEvent(Long filterId, String eventIdString) {
    this.filterId = filterId;
    this.eventIdString = eventIdString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterEvent that = (SubscriberFilterEvent) o;
    return Objects.equals(filterId, that.filterId) && Objects.equals(eventIdString, that.eventIdString);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filterId, eventIdString);
  }
}