package com.prosilion.superconductor.service.request;

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
    // TODO: refactor cleanup once all getters are done
    FiltersList filtersList = new FiltersList();
    List<Long> filterIds = getFilterIds(subscriberId);
    for (Long filterId : filterIds) {
      Filters filter = new Filters();
      filter.setEvents(getFilterEventsIds(filterId));
      filtersList.add(filter);
    }
    return filtersList;
  }

  private List<Long> getFilterIds(Long subscriberId) {
    return subscriberFilterRepository.findAllBySubscriberId(subscriberId).orElseThrow().stream().map(SubscriberFilter::getId).toList();
  }

  private EventList getFilterEventsIds(Long filterIds) {
    /**
     *  stream variation of same logic below, commented here for efficiency later
     *     List<SubscriberFilterEvent> list = subscriberFilterEventRepository.findSubscriberFilterEventsByFilterId(filterIds).orElseThrow().stream().toList();
     *     EventList eventList = new EventList();
     *
     *     list.stream().map(s -> {
     *       Optional<List<EventEntity>> result = subscriberFilterEventRepository.findEventsBySubscriberFilterEventString(s.getEventId());
     *       return result.orElseThrow().stream().map(EventEntity::convertEntityToDto).toList().stream().filter(Objects::nonNull).map(GenericEvent.class::cast).toList();
     *     }).toList().forEach(eventList::addAll);
     *
     *     return eventList;
     */

    List<SubscriberFilterEvent> list = subscriberFilterEventRepository.findSubscriberFilterEventsByFilterId(filterIds).orElseThrow().stream().toList();

    EventList eventList = new EventList();
    for (SubscriberFilterEvent s : list) {
      eventList.addAll(
          subscriberFilterEventRepository.findEventsBySubscriberFilterEventString(s.getEventId())
              .orElseThrow()
              .stream().map(EventEntity::convertEntityToDto).toList().stream().filter(Objects::nonNull)
              .map(GenericEvent.class::cast)
              .toList());
    }

    return eventList;
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

  private void saveEvents(Long filterId, EventList incomingEvents) {
    incomingEvents.getList().forEach(event -> subscriberFilterEventRepository.save(filterId, event.getId()));
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
