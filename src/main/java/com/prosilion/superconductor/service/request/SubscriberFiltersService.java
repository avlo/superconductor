package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.pubsub.AddSubscriberFiltersEvent;
import nostr.event.list.FiltersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
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

  @Async
  public void save(Long subscriberId, FiltersList filtersList) {
    subscriberFiltersManager.saveFilters(subscriberId, filtersList);
    publisher.publishEvent(new AddSubscriberFiltersEvent(subscriberId, filtersList));
  }

  @Async
  public void deleteBySubscriberId(Long subscriberId) {
    subscriberFiltersManager.removeAllFilters(subscriberId);
  }
}
