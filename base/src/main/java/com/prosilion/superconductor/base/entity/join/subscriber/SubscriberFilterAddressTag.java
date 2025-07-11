package com.prosilion.superconductor.base.entity.join.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterAddressTag extends AbstractFilterType {
  private String filterField;

  public SubscriberFilterAddressTag(Long filterId, String addressTag) {
    super(filterId, "addressTag");
    this.filterField = addressTag;
  }

  @Override
  public Object get() {
    return filterField;
  }
}
