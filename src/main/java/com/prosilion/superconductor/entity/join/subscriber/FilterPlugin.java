package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.AbstractSubscriberFilterTypeJoinRepository;

import java.util.List;

public interface FilterPlugin<
    S extends AbstractSubscriberFilterType, // @MappedSuperclass for below
    T extends AbstractSubscriberFilterTypeJoinRepository<S>> {

  String getCode();

  T getAbstractSubscriberFilterTypeJoinRepository();

  default List<S> getFilters(Long filterId) {
    return getAbstractSubscriberFilterTypeJoinRepository()
        .getAllByFilterId(filterId);
  }

  default void saveFilter(S filterType) {
    getAbstractSubscriberFilterTypeJoinRepository().save(filterType);
  }
}
