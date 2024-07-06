package com.prosilion.superconductor.entity.join.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterReferencedEvent extends AbstractFilterType {
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

  @Override
  public String getCode() {
    return "referencedEvent";
  }
}