package com.prosilion.superconductor.base.service.request.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterGenericTagQuery extends AbstractFilterType {
  private String filterField;

  public SubscriberFilterGenericTagQuery(Long filterId, String genericTagString) {
    super(filterId, "genericTag");
    this.filterField = genericTagString;
  }

  @Override
  public Object get() {
    return filterField;
  }
}
