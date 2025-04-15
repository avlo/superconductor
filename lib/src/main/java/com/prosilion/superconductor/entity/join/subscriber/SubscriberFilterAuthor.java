package com.prosilion.superconductor.entity.join.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterAuthor extends AbstractFilterType {
  private String filterField;

  public SubscriberFilterAuthor(Long filterId, String author) {
    super(filterId, "author");
    this.filterField = author;
  }

  @Override
  public Object get() {
    return filterField;
  }
}
