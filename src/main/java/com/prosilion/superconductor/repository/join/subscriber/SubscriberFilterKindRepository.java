package com.prosilion.superconductor.repository.join.subscriber;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterKind;
import nostr.event.Kind;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberFilterKindRepository<T extends SubscriberFilterKind> extends AbstractSubscriberFilterTypeJoinRepository<T> {
  void deleteAllByFilterIdIn(List<Long> filterId);

  default void save(Long filterId, Kind kind) {
    save((T) new SubscriberFilterKind(filterId, kind.getValue()));
  }
}