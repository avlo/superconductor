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
@Table(name = "subscriber-filter_referenced_event")
public class SubscriberFilterReferencedEvent extends AbstractSubscriberFilter {
  private String referencedEventId;

  public SubscriberFilterReferencedEvent(Long filterId, String referencedEventId) {
    super(filterId);
    this.referencedEventId = referencedEventId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterReferencedEvent that = (SubscriberFilterReferencedEvent) o;
    return Objects.equals(referencedEventId, that.referencedEventId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(referencedEventId);
  }
}