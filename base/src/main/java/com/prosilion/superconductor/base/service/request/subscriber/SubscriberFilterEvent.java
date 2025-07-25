package com.prosilion.superconductor.base.service.request.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterEvent extends AbstractFilterType {
  private String filterField;

  public SubscriberFilterEvent(Long filterId, String eventIdString) {
    super(filterId, "event");
    this.filterField = eventIdString;
  }

  @Override
  public Object get() {
    return filterField;
  }
}
