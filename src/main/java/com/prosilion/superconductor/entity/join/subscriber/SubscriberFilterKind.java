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
public class SubscriberFilterKind extends AbstractFilterType {
  private Integer kind;

  public SubscriberFilterKind(Long filterId, Integer kind) {
    super(filterId);
    this.kind = kind;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterKind that = (SubscriberFilterKind) o;
    return Objects.equals(kind, that.kind);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(kind);
  }

  @Override
  public String getCode() {
    return "kind";
  }
}