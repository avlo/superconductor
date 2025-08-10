package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.DeletionEventIF;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentIF;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class RedisCacheService implements RedisCacheServiceIF {
  private final EventDocumentService eventDocumentService;
  private final DeletionEventDocumentService deletionEventDocumentService;

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
    List<EventDocumentIF> all = eventDocumentService.getAll();
    List<DeletionEventDocumentIF> deletionEventEntities = getAllDeletionEvents();

    return all.stream()
        .filter(eventDocumentIF ->
            !deletionEventEntities.stream()
                .map(DeletionEventIF::getId)
                .toList()
                .contains(eventDocumentIF.getEventId())).toList();
  }

  //  @Override
//  public void deleteEventOld(@NonNull EventIF eventIF) {
//    Function<EventDocumentIF, String> getUid = EventDocumentIF::getId;

  /// /    Consumer<String> addDeletionEvent = deletionEventDocumentService::addDeletionEvent;
//    Consumer<EventDocumentIF> addDeletionEvent = deletionEventDocumentService::addDeletionEvent;
//    eventIF.getTags().stream()
//        .filter(EventTag.class::isInstance)
//        .map(EventTag.class::cast)
//        .map(EventTag::getIdEvent)
//        .map(this::getEventByEventId)
//        .flatMap(Optional::stream).toList().stream()
//        .filter(deletionCandidate ->
//            deletionCandidate.getPublicKey().equals(eventIF.getPublicKey()))
//        .forEach(addDeletionEvent);
//  }
  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    deleteEventTags(eventIF, deletionEventDocumentService::addDeletionEvent);
  }

  @Override
  public List<DeletionEventDocumentIF> getAllDeletionEvents() {
    return deletionEventDocumentService.getAll();
  }
}
