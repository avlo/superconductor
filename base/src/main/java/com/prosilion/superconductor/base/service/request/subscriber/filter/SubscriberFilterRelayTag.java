package com.prosilion.superconductor.base.service.request.subscriber.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterRelayTag extends AbstractFilterType {
  private String filterField;

  public SubscriberFilterRelayTag(Long filterId, String relayTag) {
    super(filterId, "relayTag");
    this.filterField = relayTag;
  }

  @Override
  public Object get() {
    return filterField;
  }
}
