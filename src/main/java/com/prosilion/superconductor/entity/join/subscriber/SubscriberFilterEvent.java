package com.prosilion.superconductor.entity.join.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Component
@Cacheable("subscriber-filter_event")
public class SubscriberFilterEvent extends AbstractFilterType {
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

  @Override
  public String getCode() {
    return "event";
  }
}