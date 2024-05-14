package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.SubscriberFilterReferencedEvent;
import nostr.event.impl.GenericEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberFilterReferencedEventRepository extends JpaRepository<SubscriberFilterReferencedEvent, Long> {
  void deleteByFilterId(Long filterId);

  default void save(Long filterId, GenericEvent referencedEvent) {
    this.save(new SubscriberFilterReferencedEvent(filterId, referencedEvent.getId()));
  }
}