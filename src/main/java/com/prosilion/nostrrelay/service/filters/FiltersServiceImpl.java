package com.prosilion.nostrrelay.service.filters;

import nostr.event.impl.Filters;
import nostr.event.list.EventList;
import nostr.event.list.GenericTagQueryList;
import nostr.event.list.KindList;
import nostr.event.list.PublicKeyList;
public class FiltersServiceImpl {
  private final Filters filters;

  public FiltersServiceImpl(Filters filters) {
    this.filters = filters;
  }

  public void processFilters() {
    EventList eventList = filters.getEvents();
    PublicKeyList authors = filters.getAuthors();
    KindList kindList = filters.getKinds();
    EventList referencedEvents = filters.getReferencedEvents();
    PublicKeyList referencedPubKeys = filters.getReferencePubKeys();
    Long since = filters.getSince();
    Long until = filters.getUntil();
    int limit = filters.getLimit();
    GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
  }
}
