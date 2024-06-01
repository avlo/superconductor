package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterKind;
import nostr.event.Kind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberFilterKindRepository extends JpaRepository<SubscriberFilterKind, Long> {
  void deleteAllByFilterIdIn(List<Long> filterId);

  default void save(Long filterId, Kind kind) {
    this.save(new SubscriberFilterKind(filterId, kind.getValue()));
  }
}