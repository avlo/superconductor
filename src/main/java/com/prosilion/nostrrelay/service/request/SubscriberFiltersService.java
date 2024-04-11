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
    // TODO: below save() call might also suffice for subscriber/subscriber-filters update?
    subscriberFiltersManager.saveFilters(subscriberId, filtersList);

    // TODO: potentially replace FilterList with custom/efficient data-structure/predicate if advantageous to do so
    /**
     * {@link AddSubscriberFiltersEvent} is registered & used by
     * {@link com.prosilion.nostrrelay.service.EventNotifierEngine} (and not SubscriberPool)
     */
    publisher.publishEvent(new AddSubscriberFiltersEvent(subscriberId, filtersList));
  }
}
