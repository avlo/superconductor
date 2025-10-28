package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.DeletionEventIF;
import com.prosilion.superconductor.base.util.MissingMatchingEventException;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import java.util.List;
import java.util.Objects;
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
  public Long saveWithEventTags(
      @NonNull EventIF event,
      @NonNull List<EventIF> eventTagEvents) {
    final List<String> eventTagIds = Filterable.getTypeSpecificTagsStream(EventTag.class, event)
        .map(EventTag::getIdEvent).toList();
    final List<String> eventTagEventIds = eventTagEvents.stream().map(EventIF::getId).toList();

    List<String> nonMatchingEventTagIds = eventTagIds.stream()
        .filter(eventTagEventIds::contains)
        .toList();
    assert Objects.equals(0, nonMatchingEventTagIds.size()) :
        new MissingMatchingEventException(nonMatchingEventTagIds, true);

    List<String> nonMatchingEventTagEventIds = eventTagEventIds.stream()
        .filter(eventTagIds::contains)
        .toList();
    assert Objects.equals(0, nonMatchingEventTagEventIds.size()) :
        new MissingMatchingEventException(nonMatchingEventTagEventIds, false);

    eventTagEvents.forEach(this::save);
    return save(event);
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
