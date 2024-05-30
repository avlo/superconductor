package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.BaseTagEntity;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilter;
import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterEvent;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterAuthorRepository;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterEventRepository;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterKindRepository;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterReferencedEventRepository;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterReferencedPubkeyRepository;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterRepository;
import jakarta.transaction.Transactional;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.list.EventList;
import nostr.event.list.FiltersList;
import nostr.event.list.KindList;
import nostr.event.list.PublicKeyList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

  /**
   * SELECTS
   */

  public FiltersList getSubscriberFilters(Long subscriberId) {
    FiltersList filtersList = new FiltersList();

    getFilterIds(subscriberId).forEach(subscriberFilterId -> {
      Filters filters = new Filters();
      filters.setEvents(getFilterEvents(subscriberFilterId));
      filtersList.add(filters);
    });

    return filtersList;
  }

  private List<Long> getFilterIds(Long subscriberId) {
    List<Long> list = subscriberFilterRepository.findAllBySubscriberId(subscriberId).orElseThrow().stream().map(SubscriberFilter::getId).toList();
    return list;
  }

  private EventList<GenericEvent> getFilterEvents(Long filterIds) {
    List<GenericEvent> list = subscriberFilterEventRepository.findSubscriberFilterEventsByFilterId(filterIds).orElseThrow()
        .stream().flatMap(subscriberFilterEvent ->
            getEventEntities(subscriberFilterEvent).stream().map(EventEntity::convertEntityToDto).toList().stream()
                .filter(Objects::nonNull).map(GenericEvent.class::cast)).toList();
    return new EventList<>(list);
  }

  private List<EventEntity> getEventEntities(SubscriberFilterEvent s) {
    List<EventEntity> list = subscriberFilterEventRepository.findEventsBySubscriberFilterEventString(s.getEventId())
        .orElseThrow()
        .stream().toList();
    list.forEach(entity -> entity.setTags(getBaseTags(entity.getId())));
    return list;
  }

  private List<BaseTag> getBaseTags(Long eventEntityId) {
    List<BaseTag> list = subscriberFilterEventRepository.findBaseTagsByEventEntityId(eventEntityId).orElseThrow()
        .stream().map(BaseTagEntity::convertEntityToDto).toList().stream().filter(Objects::nonNull)
        .map(BaseTag.class::cast)
        .toList();
    return list;
  }

  /**
   * SAVES
   */

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

  private void saveEvents(Long filterId, EventList<GenericEvent> incomingEvents) {
    incomingEvents.getList().forEach(event -> subscriberFilterEventRepository.save(filterId, event.getId()));
  }

  private void saveAuthors(Long filterId, PublicKeyList<PublicKey> incomingAuthors) {
    incomingAuthors.getList().forEach(author -> subscriberFilterAuthorRepository.save(filterId, author));
  }

  private void saveKinds(Long filterId, KindList incomingKinds) {
    incomingKinds.getList().forEach(kind -> subscriberFilterKindRepository.save(filterId, kind));
  }

  private void saveReferencedEvents(Long filterId, EventList<GenericEvent> incomingReferencedEvents) {
    incomingReferencedEvents.getList().forEach(referencedEvent -> subscriberFilterReferencedEventRepository.save(filterId, referencedEvent));
  }

  private void saveReferencedPubkeys(Long filterId, PublicKeyList<PublicKey> incomingReferencedPubKeys) {
    incomingReferencedPubKeys.getList().forEach(referencedPubKey -> subscriberFilterReferencedPubkeyRepository.save(filterId, referencedPubKey));
  }

  /**
   * REMOVES
   */

  public void removeAllFilters(Long subscriberId) {
    List<Long> filterId = getFilterIds(subscriberId);
    removeAuthors(filterId);
    removeKinds(filterId);
    removeReferencedEvents(filterId);
    removeReferencedPubkeys(filterId);
    removeEvents(filterId);
    removeFilters(filterId);
    //      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
  }

  private void removeFilters(List<Long> subscriberIds) {
    subscriberFilterRepository.deleteAllBySubscriberIdIn(subscriberIds);
  }

  public void removeEvents(List<Long> filterIds) {
    subscriberFilterEventRepository.deleteAllByFilterIdIn(filterIds);
  }

  public void removeAuthors(List<Long> filterIds) {
    subscriberFilterAuthorRepository.deleteAllByFilterIdIn(filterIds);
  }

  public void removeKinds(List<Long> filterIds) {
    subscriberFilterKindRepository.deleteAllByFilterIdIn(filterIds);
  }

  public void removeReferencedEvents(List<Long> filterIds) {
    subscriberFilterReferencedEventRepository.deleteAllByFilterIdIn(filterIds);
  }

  public void removeReferencedPubkeys(List<Long> filterIds) {
    subscriberFilterReferencedPubkeyRepository.deleteAllByFilterIdIn(filterIds);
  }
}
