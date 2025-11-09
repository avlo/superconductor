package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
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
  private final KindClassMapService kindClassMapService;

  public JpaCacheService(
      @NonNull EventJpaEntityService eventJpaEntityService,
      @NonNull DeletionEventJpaEntityService deletionEventJpaEntityService,
      @NonNull KindClassMapService kindClassMapService) {
    this.eventJpaEntityService = eventJpaEntityService;
    this.deletionEventJpaEntityService = deletionEventJpaEntityService;
    this.kindClassMapService = kindClassMapService;
  }

  @Override
  public BaseEvent createBaseEvent(@NonNull GenericEventRecord genericEventRecord) {
    return kindClassMapService.createBaseEvent(genericEventRecord);
  }

  @Override
  public BaseEvent save(@NonNull EventIF event) {
    return createBaseEventFromEntityIF(
        getEventByUid(
            eventJpaEntityService.save(event))
            .orElseThrow());
  }

  @Override
  public Optional<? extends BaseEvent> getEventByEventId(@NonNull String eventId) {
    Optional<EventJpaEntityIF> byEventIdString = eventJpaEntityService.findByEventIdString(eventId);
    Optional<? extends BaseEvent> t = byEventIdString.map(this::createBaseEventFromEntityIF);
    return t;
  }

  @Override
  public Optional<? extends BaseEvent> getEventByUid(@NonNull Long id) {
    Optional<EventJpaEntityIF> eventByUid = eventJpaEntityService.getEventByUid(id);
    Optional<? extends BaseEvent> first = eventByUid.map(this::createBaseEventFromEntityIF);
    return first;
  }

  @Override
  public Optional<? extends BaseEvent> getEvent(@NonNull EventIF eventIF) {
    Optional<EventJpaEntityIF> byEventIdString = eventJpaEntityService.findByEventIdString(eventIF.getId());
    Optional<? extends BaseEvent> first = byEventIdString.map(this::createBaseEventFromEntityIF);
    return first;
  }

  @Override
  public List<? extends BaseEvent> getByKind(@NonNull Kind kind) {
    List<EventJpaEntityIF> eventsByKind = eventJpaEntityService.getEventsByKind(kind);
    List<? extends BaseEvent> collect = eventsByKind.stream().map(this::createBaseEventFromEntityIF).toList();
    return collect;
  }

//  @Override
//  public Long save(@NonNull EventIF event) {
//    return eventJpaEntityService.saveEvent(event);
//  }

//  @Override
//  public Long saveWithEventTags(
//      @NonNull EventIF event,
//      @NonNull List<EventIF> eventTagEvents) {
//    final List<String> eventTagIds = Filterable.getTypeSpecificTagsStream(EventTag.class, event)
//        .map(EventTag::getIdEvent).toList();
//    final List<String> eventTagEventIds = eventTagEvents.stream().map(EventIF::getId).toList();
//
//    List<String> nonMatchingEventTagIds = eventTagIds.stream()
//        .filter(eventTagEventIds::contains)
//        .toList();
//    assert Objects.equals(0, nonMatchingEventTagIds.size()) :
//        new MissingMatchingEventException(nonMatchingEventTagIds, true);
//
//    List<String> nonMatchingEventTagEventIds = eventTagEventIds.stream()
//        .filter(eventTagIds::contains)
//        .toList();
//    assert Objects.equals(0, nonMatchingEventTagEventIds.size()) :
//        new MissingMatchingEventException(nonMatchingEventTagEventIds, false);
//
//    eventTagEvents.forEach(this::save);
//    return save(event);
//  }

  @Override
  public List<? extends BaseEvent> getAll() {
    List<EventJpaEntityIF> list = eventJpaEntityService.getAll().stream()
        .filter(eventJpaEntityIF ->
            !getAllDeletionEventIds().stream()
//                .map(DeletionEventIF::getId)
                .toList()
                .contains(eventJpaEntityIF.getUid())).toList();
    List<? extends BaseEvent> list1 = list.stream().map(this::createBaseEventFromEntityIF).toList();

    List<? extends BaseEvent> list2 = list1.stream().toList();

    return list2;
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    deleteEventTags(eventIF, deletionEventJpaEntityService::addDeletionEvent);
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
