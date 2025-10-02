package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.DeletionEventIF;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class JpaCacheService implements JpaCacheServiceIF {
  private final EventJpaEntityService eventJpaEntityService;
  private final DeletionEventJpaEntityService deletionEventJpaEntityService;

  public JpaCacheService(
      @NonNull EventJpaEntityService eventJpaEntityService,
      @NonNull DeletionEventJpaEntityService deletionEventJpaEntityService) {
    this.eventJpaEntityService = eventJpaEntityService;
    this.deletionEventJpaEntityService = deletionEventJpaEntityService;
  }

  @Override
  public Optional<EventJpaEntityIF> getEventByEventId(@NonNull String eventId) {
    return eventJpaEntityService.findByEventIdString(eventId);
  }

  @Override
  public Optional<EventJpaEntityIF> getEventByUid(@NonNull Long id) {
    return eventJpaEntityService.getEventByUid(id);
  }

  @Override
  public List<EventJpaEntityIF> getByKind(@NonNull Kind kind) {
    return eventJpaEntityService.getEventsByKind(kind);
  }

  @Override
  public Long save(@NonNull EventIF event) {
    return eventJpaEntityService.saveEvent(event);
  }

  @Override
  public List<EventJpaEntityIF> getAll() {
    return eventJpaEntityService.getAll().stream()
        .filter(eventJpaEntityIF ->
            !getAllDeletionEvents().stream()
                .map(DeletionEventIF::getId)
                .toList()
                .contains(eventJpaEntityIF.getUid())).toList();
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    deleteEventTags(eventIF, deletionEventJpaEntityService::addDeletionEvent);
  }

  @Override
  public List<DeletionEventJpaEntityIF> getAllDeletionEvents() {
    return deletionEventJpaEntityService.findAll();
  }
}
