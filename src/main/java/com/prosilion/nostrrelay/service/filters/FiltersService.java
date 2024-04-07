package com.prosilion.nostrrelay.service.filters;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.entity.join.SubscriberFilterAuthor;
import com.prosilion.nostrrelay.entity.join.SubscriberFilterEvent;
import com.prosilion.nostrrelay.repository.join.SubscriberFilterAuthorRepository;
import com.prosilion.nostrrelay.repository.join.SubscriberFilterEventRepository;
import nostr.event.impl.Filters;
import nostr.event.list.EventList;
import nostr.event.list.FiltersList;
import nostr.event.list.PublicKeyList;
import org.springframework.stereotype.Service;

@Service
public class FiltersService {
  private final SubscriberFilterEventRepository subscriberFilterEventRepository;
  private final SubscriberFilterAuthorRepository subscriberFilterAuthorRepository;

  public FiltersService() {
    this.subscriberFilterEventRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFilterEventRepository.class);
    this.subscriberFilterAuthorRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFilterAuthorRepository.class);
  }

  public void processFilters(Subscriber subscriber, FiltersList filtersList) {
    for (Filters filters : filtersList.getList()) {
      //      TODO: all below can be parallelized
      saveEvents(subscriber, filters.getEvents());
      saveAuthors(subscriber, filters.getAuthors());
      //      KindList kindList = filters.getKinds();
      //      EventList referencedEvents = filters.getReferencedEvents();
      //      PublicKeyList referencedPubKeys = filters.getReferencePubKeys();
      //      Long since = filters.getSince();
      //      Long until = filters.getUntil();
      //      int limit = filters.getLimit();
      //      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
    }
  }

  public void saveEvents(Subscriber subscriber, EventList eventList) {
    eventList.getList().iterator().forEachRemaining(event ->
        subscriberFilterEventRepository.save(
            new SubscriberFilterEvent(subscriber.getId(), event.getId())));
  }

  public void saveAuthors(Subscriber subscriber, PublicKeyList authorList) {
    authorList.getList().iterator().forEachRemaining(author ->
        subscriberFilterAuthorRepository.save(
            new SubscriberFilterAuthor(subscriber.getId(), author.toString())));
  }

}
