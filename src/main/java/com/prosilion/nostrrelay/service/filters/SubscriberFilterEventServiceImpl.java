package com.prosilion.nostrrelay.service.filters;

import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.entity.SubscriberFilterEvent;
import com.prosilion.nostrrelay.repository.SubscriberFilterEventRepository;
import jdk.jfr.Event;
import nostr.event.list.EventList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class SubscriberFilterEventServiceImpl {
  private final SubscriberFilterEventRepository subscriberFilterEventRepository;

  @Autowired
  public SubscriberFilterEventServiceImpl(SubscriberFilterEventRepository subscriberFilterEventRepository) {
    this.subscriberFilterEventRepository = subscriberFilterEventRepository;
  }

  public void process(Subscriber subscriber, EventList eventList) {
    eventList.getList().iterator().forEachRemaining(genericEvent ->
        subscriberFilterEventRepository.save(
            new SubscriberFilterEvent(subscriber, genericEvent.getId())));
  }
}
