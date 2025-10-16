package com.prosilion.superconductor.base.service.request.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterExternalIdentityTag extends AbstractFilterType {
  private String filterField;

  public SubscriberFilterExternalIdentityTag(Long filterId, String externalIdentityTag) {
    super(filterId, "externalIdentityTag");
    this.filterField = externalIdentityTag;
  }

  @Override
  public Object get() {
    return filterField;
  }
}
