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
@Table(name = "subscriber-filter_referenced_event")
public class SubscriberFilterReferencedEvent extends AbstractSubscriberFilter {
  private String referencedEventId;

  public SubscriberFilterReferencedEvent(Long filterId, String referencedEventId) {
    super(filterId);
    this.referencedEventId = referencedEventId;
  }
}