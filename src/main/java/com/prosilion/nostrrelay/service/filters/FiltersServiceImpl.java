package com.prosilion.nostrrelay.service.filters;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.Subscriber;
import nostr.event.impl.Filters;
import nostr.event.list.EventList;
import nostr.event.list.FiltersList;
public class FiltersServiceImpl {
  private final FiltersList filtersList;
  private final SubscriberFilterEventService subscriberFilterEventService;

  public FiltersServiceImpl(FiltersList filtersList) {
    this.filtersList = filtersList;
    this.subscriberFilterEventService = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFilterEventService.class);
  }

  public void processFilters(Subscriber subscriber) {
    for (Filters filters : filtersList.getList()) {
      EventList eventList = filters.getEvents();
      subscriberFilterEventService.save(subscriber, eventList);
      //      PublicKeyList authors = filters.getAuthors();
      //      KindList kindList = filters.getKinds();
      //      EventList referencedEvents = filters.getReferencedEvents();
      //      PublicKeyList referencedPubKeys = filters.getReferencePubKeys();
      //      Long since = filters.getSince();
      //      Long until = filters.getUntil();
      //      int limit = filters.getLimit();
      //      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
    }
  }
}
