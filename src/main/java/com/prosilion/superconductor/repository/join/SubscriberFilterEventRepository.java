package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.SubscriberFilterEvent;
import nostr.event.impl.GenericEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberFilterEventRepository extends JpaRepository<SubscriberFilterEvent, Long> {
  Optional<List<SubscriberFilterEvent>> findSubscriberFilterEventsByFilterId(Long filterId);
  void deleteByFilterId(Long filterId);

  default void save(Long filterId, GenericEvent genericEvent) {
    this.save(new SubscriberFilterEvent(filterId, genericEvent.getId()));
  }
}