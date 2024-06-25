package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber-filter_event")
public class SubscriberFilterEvent extends AbstractSubscriberFilter {
  private String eventIdString;

  public SubscriberFilterEvent(Long filterId, String eventIdString) {
    super(filterId);
    this.eventIdString = eventIdString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterEvent that = (SubscriberFilterEvent) o;
    return Objects.equals(eventIdString, that.eventIdString);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventIdString);
  }
}