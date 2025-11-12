package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
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
  public GenericEventRecord save(@NonNull EventIF event) {
    return createGenericEventRecordFromEntityIF(
        getJpaEventByUid(
            eventJpaEntityService.save(event))
            .orElseThrow());
  }

  @Override
  public Optional<GenericEventRecord> getEventByEventId(@NonNull String eventId) {
    Optional<EventJpaEntityIF> byEventIdString = eventJpaEntityService.findByEventIdString(eventId);
    Optional<GenericEventRecord> t = byEventIdString.map(this::createGenericEventRecordFromEntityIF);
    return t;
  }

  @Override
  public Optional<GenericEventRecord> getJpaEventByUid(Long id) {
    Optional<EventJpaEntityIF> eventByUid = eventJpaEntityService.getEventByUid(id);
    Optional<GenericEventRecord> first = eventByUid.map(this::createGenericEventRecordFromEntityIF);
    return first;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull EventIF eventIF) {
    Optional<EventJpaEntityIF> byEventIdString = eventJpaEntityService.findByEventIdString(eventIF.getId());
    Optional<GenericEventRecord> first = byEventIdString.map(this::createGenericEventRecordFromEntityIF);
    return first;
  }

  @Override
  public List<GenericEventRecord> getByKind(@NonNull Kind kind) {
    List<EventJpaEntityIF> eventsByKind = eventJpaEntityService.getEventsByKind(kind);
    List<GenericEventRecord> collect = eventsByKind.stream().map(this::createGenericEventRecordFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<GenericEventRecord> getAll() {
    List<EventJpaEntityIF> list = eventJpaEntityService.getAll().stream()
        .filter(eventJpaEntityIF ->
            !getAllDeletionEventIds().stream()
//                .map(DeletionEventIF::getId)
                .toList()
                .contains(eventJpaEntityIF.getUid())).toList();
    List<GenericEventRecord> genericEventRecords = list.stream().map(this::createGenericEventRecordFromEntityIF).toList();
    return genericEventRecords;
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    eventJpaEntityService.findByEventIdString(eventIF.getId()).ifPresent(deletionEventJpaEntityService::addDeletionEvent);
//    TODO: revisit below rationale:
//    do not delete eventTags as they are/likely referenced by other events 
//    deleteEventTags(eventIF, deletionEventJpaEntityService::addDeletionEvent);
  }

  @Override
  public List<Long> getAllDeletionEventIds() {
    List<DeletionEventJpaEntityIF> all = deletionEventJpaEntityService.getAll();
    return all.stream().map(DeletionEventJpaEntityIF::getEventId).toList();
  }

  void deleteEventTags(
      @NonNull EventIF eventIF,
      @NonNull Consumer<EventJpaEntityIF> addDeletionEvent) {
    eventIF.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent)
        .map(eventJpaEntityService::findByEventIdString)
        .flatMap(Optional::stream)
        .filter(deletionCandidate ->
            deletionCandidate.getPublicKey().equals(eventIF.getPublicKey()))
        .forEach(addDeletionEvent);
  }
}
