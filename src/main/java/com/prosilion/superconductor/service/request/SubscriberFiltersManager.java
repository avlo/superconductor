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
import nostr.event.Kind;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

  public List<SubscriberFilter> getEntireSubscribersFilters() {
    return subscriberFilterRepository.findAll();
  }

  /**
   * SELECTS
   */

  public List<Filters> getSubscriberFilters(Long subscriberId) {
    List<Filters> filtersList = new ArrayList<>();

    List<Long> filterIds = getFilterIds(subscriberId);
    filterIds.forEach(subscriberFilterId -> {
      List<GenericEvent> filterEvents = getFilterEvents(subscriberFilterId);
      boolean empty = filterEvents.isEmpty();
      int size = filterEvents.size();  // ide says size > 0 is always false
      boolean is = size > 0;
      int size1 = filterEvents.size();
      if (!empty || (is) ||(size1 > 0)) {
        Filters filters = new Filters();
        filters.setEvents(filterEvents);
        filtersList.add(filters);
      }
    });

    return filtersList;
  }

  private List<Long> getFilterIds(Long subscriberId) {
    return subscriberFilterRepository.findAllBySubscriberId(subscriberId).stream().map(SubscriberFilter::getId).toList();
  }

  private List<GenericEvent> getFilterEvents(Long filterIds) {
    List<GenericEvent> list = subscriberFilterEventRepository.findSubscriberFilterEventsByFilterId(filterIds)
        .stream().flatMap(subscriberFilterEvent ->
            getEventEntities(subscriberFilterEvent).stream().map(EventEntity::convertEntityToDto).toList().stream()
                .filter(Objects::nonNull).map(GenericEvent.class::cast)).toList();

//    Stream<Stream<GenericEvent>> streamStream = subscriberFilterEventRepository.findSubscriberFilterEventsByFilterId(filterIds)
//        .stream().map(subscriberFilterEvent ->
//            getEventEntities(subscriberFilterEvent).stream().map(EventEntity::convertEntityToDto).toList().stream()
//                .filter(Objects::nonNull).map(GenericEvent.class::cast));


    List<GenericEvent> genericEvents = new ArrayList<>(list);
    return genericEvents;
  }

  private List<EventEntity> getEventEntities(SubscriberFilterEvent s) {
    List<EventEntity> list = subscriberFilterEventRepository.findEventsBySubscriberFilterEventString(s.getEventId())
        .stream().toList();
    list.forEach(entity -> entity.setTags(getBaseTags(entity.getId())));
    return list;
  }

  private List<BaseTag> getBaseTags(Long eventEntityId) {
    List<BaseTag> list = subscriberFilterEventRepository.findBaseTagsByEventEntityId(eventEntityId)
        .stream().map(BaseTagEntity::convertEntityToDto).toList().stream().filter(Objects::nonNull)
        .map(BaseTag.class::cast)
        .toList();
    return list;
  }

  /**
   * SAVES
   */

  public void saveFilters(Long subscriberId, List<Filters> filtersList) {
    for (Filters filters : filtersList) {
      Long filterId = saveSubscriberFilter(subscriberId, filters);
      saveEvents(filterId, Optional.ofNullable(filters.getEvents()).orElseGet(ArrayList::new));
      saveAuthors(filterId, Optional.ofNullable(filters.getAuthors()).orElseGet(ArrayList::new));
      saveKinds(filterId, Optional.ofNullable(filters.getKinds()).orElseGet(ArrayList::new));
      saveReferencedEvents(filterId, Optional.ofNullable(filters.getReferencedEvents()).orElseGet(ArrayList::new));
      saveReferencedPubkeys(filterId, Optional.ofNullable(filters.getReferencePubKeys()).orElseGet(ArrayList::new));
      //      GenericTagQueryList genericTagQueryList = filters.getGenericTagQueryList();
    }
  }

  private Long saveSubscriberFilter(Long subscriberId, Filters filters) {
    return subscriberFilterRepository.save(new SubscriberFilter(subscriberId, filters.getSince(), filters.getUntil(), filters.getLimit())).getId();
  }

  private void saveEvents(Long filterId, List<GenericEvent> incomingEvents) {
    incomingEvents.forEach(event -> subscriberFilterEventRepository.save(filterId, event.getId()));
  }

  private void saveAuthors(Long filterId, List<PublicKey> incomingAuthors) {
    incomingAuthors.forEach(author -> subscriberFilterAuthorRepository.save(filterId, author));
  }

  private void saveKinds(Long filterId, List<Kind> incomingKinds) {
    incomingKinds.forEach(kind -> subscriberFilterKindRepository.save(filterId, kind));
  }

  private void saveReferencedEvents(Long filterId, List<GenericEvent> incomingReferencedEvents) {
    incomingReferencedEvents.forEach(referencedEvent -> subscriberFilterReferencedEventRepository.save(filterId, referencedEvent));
  }

  private void saveReferencedPubkeys(Long filterId, List<PublicKey> incomingReferencedPubKeys) {
    incomingReferencedPubKeys.forEach(referencedPubKey -> subscriberFilterReferencedPubkeyRepository.save(filterId, referencedPubKey));
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
