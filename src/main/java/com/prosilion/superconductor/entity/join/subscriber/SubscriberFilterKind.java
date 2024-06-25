package com.prosilion.superconductor.entity.join.subscriber;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "subscriber-filter_kind")
public class SubscriberFilterKind extends AbstractSubscriberFilter {
  private Integer kindId;

  public SubscriberFilterKind(Long filterId, Integer kindId) {
    super(filterId);
    this.kindId = kindId;
  }
}