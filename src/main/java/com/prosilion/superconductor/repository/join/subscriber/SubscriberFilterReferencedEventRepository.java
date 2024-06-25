package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterReferencedEvent;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberFilterReferencedEventRepository<T extends SubscriberFilterReferencedEvent> extends AbstractSubscriberFilterTypeJoinRepository<T> {
  void deleteAllByFilterIdIn(List<Long> filterId);

  default void save(Long filterId, GenericEvent referencedEvent) {
    save((T) new SubscriberFilterReferencedEvent(filterId, referencedEvent.getId()));
  }
}