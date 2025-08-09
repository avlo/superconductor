package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.DeletionEventIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class JpaCacheService implements JpaCacheServiceIF {
  private final EventEntityService eventEntityService;
  private final DeletionEventEntityService deletionEventEntityService;

  public JpaCacheService(
      @NonNull EventEntityService eventEntityService,
      @NonNull DeletionEventEntityService deletionEventEntityService) {
    this.eventEntityService = eventEntityService;
    this.deletionEventEntityService = deletionEventEntityService;
  }

  @Override
  public Optional<EventEntityIF> getEventByEventId(@NonNull String eventId) {
    return eventEntityService.findByEventIdString(eventId);
  }

  @Override
  public Optional<EventEntityIF> getEventByUid(@NonNull Long id) {
    return eventEntityService.getEventByUid(id);
  }

  @Override
  public List<EventEntityIF> getByKind(@NonNull Kind kind) {
    return eventEntityService.getEventsByKind(kind);
  }

  @Override
  public Long save(@NonNull EventIF event) {
    return eventEntityService.saveEventEntity(event);
  }

  @Override
  public List<EventEntityIF> getAll() {
    List<EventEntityIF> all = eventEntityService.getAll();
    List<DeletionEventEntityIF> deletionEventEntities = getAllDeletionEvents();

    return all.stream()
        .filter(eventEntityIF ->
            !deletionEventEntities.stream()
                .map(DeletionEventIF::getId)
                .toList()
                .contains(eventEntityIF.getUid())).toList();
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    Function<EventEntityIF, Long> getUid = EventEntityIF::getUid;
    Consumer<Long> addDeletionEvent = deletionEventEntityService::addDeletionEvent;
    eventIF.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent)
        .map(this::getEventByEventId)
        .flatMap(Optional::stream).toList().stream()
        .filter(deletionCandidate ->
            deletionCandidate.getPublicKey().equals(eventIF.getPublicKey()))
        .map(getUid)
        .forEach(addDeletionEvent);
  }

  @Override
  public List<DeletionEventEntityIF> getAllDeletionEvents() {
    return deletionEventEntityService.findAll();
  }
}
