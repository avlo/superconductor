package com.prosilion.superconductor.base.service.request.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterReferencedEvent extends AbstractFilterType {
  private String filterField;

  public SubscriberFilterReferencedEvent(Long filterId, String referencedEventId) {
    super(filterId, "referencedEvent");
    this.filterField = referencedEventId;
  }

  @Override
  public Object get() {
    return filterField;
  }
}
