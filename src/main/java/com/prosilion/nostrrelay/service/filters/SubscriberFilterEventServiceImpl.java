package com.prosilion.nostrrelay.service.filters;

import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.entity.join.SubscriberFilterEventJoin;
import com.prosilion.nostrrelay.repository.join.SubscriberFilterEventRepositoryJoin;
import nostr.event.list.EventList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class SubscriberFilterEventServiceImpl {
  private final SubscriberFilterEventRepositoryJoin subscriberFilterEventRepositoryJoin;

  @Autowired
  public SubscriberFilterEventServiceImpl(SubscriberFilterEventRepositoryJoin subscriberFilterEventRepositoryJoin) {
    this.subscriberFilterEventRepositoryJoin = subscriberFilterEventRepositoryJoin;
  }

  public void process(Subscriber subscriber, EventList eventList) {
    eventList.getList().iterator().forEachRemaining(genericEvent ->
        subscriberFilterEventRepositoryJoin.save(
            new SubscriberFilterEventJoin(subscriber, genericEvent.getId())));
  }
}
