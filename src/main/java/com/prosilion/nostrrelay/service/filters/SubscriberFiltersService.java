package com.prosilion.nostrrelay.service.filters;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.entity.join.*;
import com.prosilion.nostrrelay.repository.join.*;
import nostr.event.impl.Filters;
import nostr.event.list.EventList;
import nostr.event.list.FiltersList;
import nostr.event.list.KindList;
import nostr.event.list.PublicKeyList;
import org.springframework.stereotype.Service;

@Service
public class SubscriberFiltersService {
  private final SubscriberFilterRepository subscriberFilterRepository;
  private final SubscriberFilterEventRepository subscriberFilterEventRepository;
  private final SubscriberFilterAuthorRepository subscriberFilterAuthorRepository;
  private final SubscriberFilterKindRepository subscriberFilterKindRepository;
  private final SubscriberFilterReferencedEventRepository subscriberFilterReferencedEventRepository;
  private final SubscriberFilterReferencedPubkeyRepository subscriberFilterReferencedPubkeyRepository;

  public SubscriberFiltersService() {
    this.subscriberFilterRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFilterRepository.class);
    this.subscriberFilterEventRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFilterEventRepository.class);
    this.subscriberFilterAuthorRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFilterAuthorRepository.class);
    this.subscriberFilterKindRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFilterKindRepository.class);
    this.subscriberFilterReferencedEventRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFilterReferencedEventRepository.class);
    this.subscriberFilterReferencedPubkeyRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFilterReferencedPubkeyRepository.class);
  }

  public void processFilters(Subscriber subscriber, FiltersList filtersList) {
    for (Filters filters : filtersList.getList()) {
      //      TODO: all below can be parallelized
      Long filterId = saveSubscriberFilter(subscriber, filters);
      saveEvents(filterId, filters.getEvents());
      saveAuthors(filterId, filters.getAuthors());
      saveKinds(filterId, filters.getKinds());
      saveReferencedEvents(filterId, filters.getReferencedEvents());
      saveReferencedPubkeys(filterId, filters.getReferencePubKeys());
      //      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
    }
  }

  private Long saveSubscriberFilter(Subscriber subscriber, Filters filters) {
    return subscriberFilterRepository.save(
        new SubscriberFilter(subscriber.getId(), filters.getSince(), filters.getUntil(), filters.getLimit())).getId();
  }

  private void saveEvents(Long filterId, EventList eventList) {
    eventList.getList().iterator().forEachRemaining(event ->
        subscriberFilterEventRepository.save(
            new SubscriberFilterEvent(filterId, event.getId())));
  }

  private void saveAuthors(Long filterId, PublicKeyList authorList) {
    authorList.getList().iterator().forEachRemaining(author ->
        subscriberFilterAuthorRepository.save(
            new SubscriberFilterAuthor(filterId, author.toString())));
  }

  private void saveKinds(Long filterId, KindList kindList) {
    kindList.getList().iterator().forEachRemaining(kind ->
        subscriberFilterKindRepository.save(
            new SubscriberFilterKind(filterId, kind)));
  }

  private void saveReferencedEvents(Long filterId, EventList eventList) {
    eventList.getList().iterator().forEachRemaining(event ->
        subscriberFilterReferencedEventRepository.save(
            new SubscriberFilterReferencedEvent(filterId, event.getId())));
  }

  private void saveReferencedPubkeys(Long filterId, PublicKeyList publicKeyList) {
    publicKeyList.getList().iterator().forEachRemaining(pubkey ->
        subscriberFilterReferencedPubkeyRepository.save(
            new SubscriberFilterReferencedPubkey(filterId, pubkey.toString())));
  }
}
