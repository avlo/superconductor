package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.DeletionEventIF;
import com.prosilion.superconductor.lib.redis.document.DeletionEventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.document.EventNosqlEntityIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class RedisCacheService implements RedisCacheServiceIF {
  private final EventNosqlEntityService eventNosqlEntityService;
  private final DeletionEventNoSqlEntityService deletionEventNoSqlEntityService;

  public RedisCacheService(
      @NonNull EventNosqlEntityService eventNosqlEntityService,
      @NonNull DeletionEventNoSqlEntityService deletionEventNoSqlEntityService) {
    this.eventNosqlEntityService = eventNosqlEntityService;
    this.deletionEventNoSqlEntityService = deletionEventNoSqlEntityService;
  }

  @Override
  public Optional<EventNosqlEntityIF> getEventByEventId(@NonNull String eventId) {
    return eventNosqlEntityService.findByEventIdString(eventId);
  }

  @Override
  public List<EventNosqlEntityIF> getByKind(@NonNull Kind kind) {
    return eventNosqlEntityService.getEventsByKind(kind);
  }

  @Override
  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey) {
    return eventNosqlEntityService.getEventsByKindAndPubKeyTag(kind, publicKey);
  }

  @Override
  public List<EventNosqlEntityIF> getEventsByKindAndUuid(Kind kind, String uuid) {
    return eventNosqlEntityService.getEventsByKindAndUuid(kind, uuid);
  }

  @Override
  public EventNosqlEntityIF save(@NonNull EventIF event) {
    return eventNosqlEntityService.saveEvent(event);
  }

  @Override
  public List<EventNosqlEntityIF> getAll() {
    return eventNosqlEntityService.getAll().stream()
        .filter(eventNosqlEntityIF ->
            !getAllDeletionEvents().stream()
                .map(DeletionEventIF::getId)
                .toList()
                .contains(eventNosqlEntityIF.getEventId())).toList();
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    deleteEventTags(eventIF, deletionEventNoSqlEntityService::addDeletionEvent);
  }

  @Override
  public List<DeletionEventNosqlEntityIF> getAllDeletionEvents() {
    return deletionEventNoSqlEntityService.getAll();
  }
}
