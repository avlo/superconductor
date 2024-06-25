package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber-filter_kind")
public class SubscriberFilterKind extends AbstractSubscriberFilterType {
  private Integer kindId;

  public SubscriberFilterKind(Long filterId, Integer kindId) {
    super(filterId);
    this.kindId = kindId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilterKind that = (SubscriberFilterKind) o;
    return Objects.equals(kindId, that.kindId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(kindId);
  }

  @Override
  public String getCode() {
    return "kind";
  }
}