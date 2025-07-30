package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.DeletionEntityIF;
import com.prosilion.superconductor.lib.jpa.dto.deletion.DeletionEventEntityJpaIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class JpaCacheService implements JpaCacheServiceIF {

  private final JpaEventEntityService jpaEventEntityService;
  private final JpaDeletionEventEntityService jpaDeletionEventEntityService;

  public JpaCacheService(
      @NonNull JpaEventEntityService jpaEventEntityService,
      @NonNull JpaDeletionEventEntityService jpaDeletionEventEntityService) {
    this.jpaEventEntityService = jpaEventEntityService;
    this.jpaDeletionEventEntityService = jpaDeletionEventEntityService;
  }

  @Override
  public Optional<EventEntityIF> getEventByEventId(@NonNull String eventId) {
    Optional<EventEntityIF> byEventIdString = jpaEventEntityService.findByEventIdString(eventId);
    return byEventIdString;
  }

//  @Override
//  public Optional<EventEntityIF> getEventByIdStringAsEventEntityIF(@NonNull Long id) {
//    return getEventById(id)
//        .map(
//            EventEntityIF::getId)
//        .flatMap(
//            eventEntityService::getEventByIdStringAsEventEntityIF);
//  }

  @Override
  public Optional<EventEntityIF> getEventByUid(@NonNull Long id) {
    Optional<EventEntityIF> eventByUid = jpaEventEntityService.getEventByUid(id);
    return eventByUid;
  }

  // impls parent IF (CacheIF) spec...
  public void deleteEventEntity(@NonNull EventIF eventIF) {
    eventIF.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent)
        .map(this::getEventByEventId)
        .flatMap(Optional::stream).toList().stream()
        .map(EventEntityIF::getUid)
        .forEach(this::deleteEventEntity);
  }

  @Override
  public List<EventEntityIF> getByKind(@NonNull Kind kind) {
    return jpaEventEntityService.getEventsByKind(kind);
  }

  @Override
  public Long save(@NonNull EventIF event) {
    return jpaEventEntityService.saveEventEntity(event);
  }


  public List<EventEntityIF> getAll() {
    return jpaEventEntityService.getAll();
  }

  @Override
  public Map<Kind, Map<?, ? extends EventIF>> getAllMappedByKind() {
    List<EventEntityIF> eventEntities = this.getAll();
//    TODO: fix missing generic    
    List<DeletionEventEntityJpaIF> deletionEventEntities = getAllDeletionJpaEventEntities();

    List<EventEntityIF> filteredDeletionEntities = eventEntities.stream().filter(eventEntityIF ->
        !deletionEventEntities.stream().map(DeletionEntityIF::getId).toList().contains(eventEntityIF.getUid())).toList();

    Map<Kind, Map<Long, EventEntityIF>> collect = getCollect(filteredDeletionEntities);

    Map<Kind, Map<?, ? extends EventIF>> filteredDeletedEventsAsWildcards = collect.entrySet().stream().collect(
        Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue));

    log.debug("filteredDeletedEventsAsWildcards {}", filteredDeletedEventsAsWildcards);
    return filteredDeletedEventsAsWildcards;
  }

  private static Map<Kind, Map<Long, EventEntityIF>> getCollect(List<EventEntityIF> list) {
    Map<Kind, Map<Long, EventEntityIF>> result =
        list.stream()
            .collect(
                Collectors.groupingBy(EventEntityIF::getKind,
                    Collectors.toMap(
                        EventEntityIF::getUid,
                        Function.identity())));
    return result;
  }

  public List<DeletionEventEntityJpaIF> getAllDeletionJpaEventEntities() {
    List<DeletionEventEntityJpaIF> all = jpaDeletionEventEntityService.findAll();
    return all;
  }

  @Override
  public void deleteEventEntity(@NonNull Long id) {
    jpaDeletionEventEntityService.addDeletionEvent(id);
  }
}
