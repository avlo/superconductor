package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.repository.join.subscriber.AbstractSubscriberFilterTypeJoinRepository;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;

import java.util.List;
import java.util.function.BiPredicate;

public interface FilterPlugin<
    T extends AbstractSubscriberFilterTypeJoinRepository<U>,
    U extends AbstractFilterType> {

  String getCode();

  void appendFilters(Long filterId, Filters filters);

  T getJoin();

  List<U> getTypeSpecificFilterList(Long filterId, Filters filters);

  BiPredicate<?, AddNostrEvent<GenericEvent>> getBiPredicate();

  List<?> getPluginFilters(Filters filters);

  default void saveFilters(Long filterId, Filters filters) {
    getJoin().saveAllAndFlush(() ->
        getTypeSpecificFilterList(filterId, filters).iterator());
  }

  default void removeFilters(Long filterId) {
    getJoin().deleteAllByFilterId(filterId);
  }
}
