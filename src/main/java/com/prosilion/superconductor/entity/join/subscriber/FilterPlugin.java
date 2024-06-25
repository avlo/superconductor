package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.AbstractSubscriberFilterTypeJoinRepository;
import nostr.event.impl.Filters;

public interface FilterPlugin {

  String getCode();

  Filters appendFilters(Long filterId, Filters filters);

  void saveFilter(Long filterId, Filters filterType);

  AbstractSubscriberFilterTypeJoinRepository<AbstractFilterType> getJoin();

  default void removeFilters(Long filterId) {
    getJoin().deleteAllByFilterId(filterId);
  }
}
