package com.prosilion.nostrrelay.service.filters;

import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.entity.join.SubscriberFilterEvent;
import com.prosilion.nostrrelay.repository.join.SubscriberFilterEventRepository;
import nostr.event.list.EventList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class SubscriberFilterEventService {
  private final SubscriberFilterEventRepository subscriberFilterEventRepository;

  @Autowired
  public SubscriberFilterEventService(SubscriberFilterEventRepository subscriberFilterEventRepository) {
    this.subscriberFilterEventRepository = subscriberFilterEventRepository;
  }

  public void save(Subscriber subscriber, EventList eventList) {
    eventList.getList().iterator().forEachRemaining(event ->
        subscriberFilterEventRepository.save(
            new SubscriberFilterEvent(subscriber.getId(), event.getId())));
  }
}
