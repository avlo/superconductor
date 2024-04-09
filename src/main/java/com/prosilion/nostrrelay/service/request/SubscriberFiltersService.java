package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.pubsub.AddSubscriberFiltersEvent;
import nostr.event.list.FiltersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class SubscriberFiltersService {
  private final SubscriberFiltersManager subscriberFiltersManager;
  private final ApplicationEventPublisher publisher;

  @Autowired
  public SubscriberFiltersService(SubscriberFiltersManager subscriberFiltersManager, ApplicationEventPublisher publisher) {
    this.subscriberFiltersManager = subscriberFiltersManager;
    this.publisher = publisher;
  }

  public void save(Long subscriberId, FiltersList filtersList) {
    // TODO: below add might also suffice for update?
    subscriberFiltersManager.saveFilters(subscriberId, filtersList);

    // add filters to engine/pool
    // also TODO: below use of FilterList might more accureately need to be custom FiltersListStructure
    //        returned from above saveFilters() method
    publisher.publishEvent(new AddSubscriberFiltersEvent(subscriberId, filtersList));   //Notify the listeners
  }
}
