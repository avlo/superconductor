package com.prosilion.superconductor.entity.join.subscriber;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilterGenericTagQuery extends AbstractFilterType {
  private String genericTagString;

  public SubscriberFilterGenericTagQuery(Long filterId, String genericTagString) {
    super(filterId);
    this.genericTagString = genericTagString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterGenericTagQuery that = (SubscriberFilterGenericTagQuery) o;
    return Objects.equals(genericTagString, that.genericTagString);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(genericTagString);
  }

  @Override
  public String getCode() {
    return "genericTag";
  }
}
