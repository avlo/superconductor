package com.prosilion.superconductor.base.service.request.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterReferencedPubkey extends AbstractFilterType {
  private String filterField;

  public SubscriberFilterReferencedPubkey(Long filterId, String referencedPubkey) {
    super(filterId, "referencedPubKey");
    this.filterField = referencedPubkey;
  }

  @Override
  public Object get() {
    return filterField;
  }
}
