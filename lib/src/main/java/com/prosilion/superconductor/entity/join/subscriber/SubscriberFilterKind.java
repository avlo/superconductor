package com.prosilion.superconductor.entity.join.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterKind extends AbstractFilterType {
  private Integer filterField;

  public SubscriberFilterKind(Long filterId, Integer kind) {
    super(filterId, "kind");
    this.filterField = kind;
  }

  @Override
  public Object get() {
    return filterField;
  }
}
