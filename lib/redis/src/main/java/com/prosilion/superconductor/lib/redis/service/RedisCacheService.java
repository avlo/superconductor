package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.user.PublicKey;
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
  public List<EventDocumentIF> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey) {
    return eventDocumentService.getEventsByKindAndPubKeyTag(kind, publicKey);
  }

  @Override
  public EventDocumentIF save(@NonNull EventIF event) {
    return eventDocumentService.saveEventDocument(event);
  }

  @Override
  public List<EventDocumentIF> getAll() {
    return eventDocumentService.getAll().stream()
        .filter(eventDocumentIF ->
            !getAllDeletionEvents().stream()
                .map(DeletionEventIF::getId)
                .toList()
                .contains(eventDocumentIF.getEventId())).toList();
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    deleteEventTags(eventIF, deletionEventDocumentService::addDeletionEvent);
  }

  @Override
  public List<DeletionEventDocumentIF> getAllDeletionEvents() {
    return deletionEventDocumentService.getAll();
  }
}
