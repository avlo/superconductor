package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentRedisIF;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisCacheService implements CacheServiceIF {
  private final EventDocumentService eventDocumentService;
  private final DeletionEventDocumentService deletionEventDocumentService;

  @Autowired
  public RedisCacheService(
      @NonNull EventDocumentService eventDocumentService,
      @NonNull DeletionEventDocumentService deletionEventDocumentService) {
    this.eventDocumentService = eventDocumentService;
    this.deletionEventDocumentService = deletionEventDocumentService;
  }

  @Override
  public Optional<EventDocumentIF> getEventByEventId(@NonNull String eventId) {
    return eventDocumentService.findByEventIdString(eventId);
  }

  @Override
  public List<EventDocumentIF> getByKind(@NonNull Kind kind) {
    return eventDocumentService.getEventsByKind(kind);
  }

  @Override
  public EventDocumentIF save(@NonNull EventIF event) {
    return eventDocumentService.saveEventDocument(event);
  }

  @Override
  public List<EventDocumentIF> getAll() {
    return eventDocumentService.getAll();
  }

  @Override
  public Map<Kind, Map<?, ? extends EventIF>> getAllMappedByKind() {
    List<EventDocumentIF> eventEntities = getAll();
//    TODO: fix missing generic
    List<DeletionEventDocumentRedisIF> deletionEventEntities = getAllDeletionEventEntities();

    List<EventDocumentIF> filteredDeletedEvents = eventEntities.stream().filter(eventEntityIF ->
        deletionEventEntities.stream()
            .anyMatch(deletionEventEntityJpaIF ->
                deletionEventEntityJpaIF.getId().equals(eventEntityIF.getId()))).toList();

    Map<Kind, Map<String, EventDocumentIF>> filteredDeletedEventsAsHardGenerics = filteredDeletedEvents.stream()
        .collect(
            Collectors.groupingBy(EventIF::getKind,
                Collectors.toMap(
                    EventDocumentIF::getId,
                    Function.identity())));

    Map<Kind, Map<?, ? extends EventIF>> filteredDeletedEventsAsWildcards =
        filteredDeletedEventsAsHardGenerics.entrySet().stream().collect(
            Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue));

    log.debug("filteredDeletedEventsAsWildcards {}", filteredDeletedEventsAsWildcards);
    return filteredDeletedEventsAsWildcards;
  }

  public List<DeletionEventDocumentRedisIF> getAllDeletionEventEntities() {
    return deletionEventDocumentService.getAll();
  }

  public void deleteEventEntity(@NonNull EventIF event) {
    Optional<EventDocumentIF> byEventIdString = getEventByEventId(event.getId());

    byEventIdString.map(EventDocumentIF::getId).ifPresent(deletionEventDocumentService::deleteEventEntity);
  }
}
