package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.join.SubscriberFilter;
import com.prosilion.superconductor.repository.join.*;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import nostr.event.impl.Filters;
import nostr.event.list.EventList;
import nostr.event.list.FiltersList;
import nostr.event.list.KindList;
import nostr.event.list.PublicKeyList;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class SubscriberFiltersManager {
  private final SubscriberFilterRepository subscriberFilterRepository;
  private final SubscriberFilterEventRepository subscriberFilterEventRepository;
  private final SubscriberFilterAuthorRepository subscriberFilterAuthorRepository;
  private final SubscriberFilterKindRepository subscriberFilterKindRepository;
  private final SubscriberFilterReferencedEventRepository subscriberFilterReferencedEventRepository;
  private final SubscriberFilterReferencedPubkeyRepository subscriberFilterReferencedPubkeyRepository;

  public SubscriberFiltersManager(
      SubscriberFilterRepository subscriberFilterRepository,
      SubscriberFilterEventRepository subscriberFilterEventRepository,
      SubscriberFilterAuthorRepository subscriberFilterAuthorRepository,
      SubscriberFilterKindRepository subscriberFilterKindRepository,
      SubscriberFilterReferencedEventRepository subscriberFilterReferencedEventRepository,
      SubscriberFilterReferencedPubkeyRepository subscriberFilterReferencedPubkeyRepository) {
    this.subscriberFilterRepository = subscriberFilterRepository;
    this.subscriberFilterEventRepository = subscriberFilterEventRepository;
    this.subscriberFilterAuthorRepository = subscriberFilterAuthorRepository;
    this.subscriberFilterKindRepository = subscriberFilterKindRepository;
    this.subscriberFilterReferencedEventRepository = subscriberFilterReferencedEventRepository;
    this.subscriberFilterReferencedPubkeyRepository = subscriberFilterReferencedPubkeyRepository;
  }

  public void saveFilters(Long subscriberId, FiltersList filtersList) {
    for (Filters filters : filtersList.getList()) {
      Long filterId = saveSubscriberFilter(subscriberId, filters);
      saveEvents(filterId, Optional.ofNullable(filters.getEvents()).orElseGet(() -> EventList.builder().build()));
      saveAuthors(filterId, Optional.ofNullable(filters.getAuthors()).orElseGet(() -> PublicKeyList.builder().build()));
      saveKinds(filterId, Optional.ofNullable(filters.getKinds()).orElseGet(() -> KindList.builder().build()));
      saveReferencedEvents(filterId, Optional.ofNullable(filters.getReferencedEvents()).orElseGet(() -> EventList.builder().build()));
      saveReferencedPubkeys(filterId, Optional.ofNullable(filters.getReferencePubKeys()).orElseGet(() -> PublicKeyList.builder().build()));
      //      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
    }
  }

  private Long saveSubscriberFilter(Long subscriberId, Filters filters) {
    return subscriberFilterRepository.save(new SubscriberFilter(subscriberId, filters.getSince(), filters.getUntil(), filters.getLimit())).getId();
  }

  private void saveEvents(Long filterId, EventList incomingEvents) {
    incomingEvents.getList().forEach(event -> subscriberFilterEventRepository.save(filterId, event));
  }

  private void saveAuthors(Long filterId, PublicKeyList incomingAuthors) {
    incomingAuthors.getList().forEach(author -> subscriberFilterAuthorRepository.save(filterId, author));
  }

  private void saveKinds(Long filterId, KindList incomingKinds) {
    incomingKinds.getList().forEach(kind -> subscriberFilterKindRepository.save(filterId, kind));
  }

  private void saveReferencedEvents(Long filterId, EventList incomingReferencedEvents) {
    incomingReferencedEvents.getList().forEach(referencedEvent -> subscriberFilterReferencedEventRepository.save(filterId, referencedEvent));
  }

  private void saveReferencedPubkeys(Long filterId, PublicKeyList incomingReferencedPubKeys) {
    incomingReferencedPubKeys.getList().forEach(referencedPubKey -> subscriberFilterReferencedPubkeyRepository.save(filterId, referencedPubKey));
  }
  /**
   * REMOVES
   */

  public void removeAllFilters(Long subscriberId) {
    Long filterId = getFilterId(subscriberId);
    removeAuthors(filterId);
    removeKinds(filterId);
    removeReferencedEvents(filterId);
    removeReferencedPubkeys(filterId);
    removeEvents(filterId);
    removeFilters(filterId);
    //      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
  }

  private Long getFilterId(Long subscriberId) {
    return subscriberFilterRepository.findBySubscriberId(subscriberId).orElseThrow(NoResultException::new).getId();
  }

  private void removeFilters(Long subscriberId) {
    subscriberFilterRepository.deleteById(subscriberId);
  }

  public void removeEvents(Long filterId) {
    subscriberFilterEventRepository.deleteByFilterId(filterId);
  }

  public void removeAuthors(Long filterId) {
    subscriberFilterAuthorRepository.deleteByFilterId(filterId);
  }

  public void removeKinds(Long filterId) {
    subscriberFilterKindRepository.deleteByFilterId(filterId);
  }

  public void removeReferencedEvents(Long filterId) {
    subscriberFilterReferencedEventRepository.deleteByFilterId(filterId);
  }

  public void removeReferencedPubkeys(Long filterId) {
    subscriberFilterReferencedPubkeyRepository.deleteByFilterId(filterId);
  }
}
