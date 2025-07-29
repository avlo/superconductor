package com.prosilion.superconductor.lib.jpa.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.DeletionEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import com.prosilion.superconductor.lib.jpa.service.DeletionEventEntityService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class JpaCache implements JpaCacheIF {

  private final EventEntityService eventEntityService;
  private final DeletionEventEntityService deletionEventEntityService;

  public JpaCache(
      @NonNull EventEntityService eventEntityService,
      @NonNull DeletionEventEntityService deletionEventEntityService) {
    this.eventEntityService = eventEntityService;
    this.deletionEventEntityService = deletionEventEntityService;
  }

  @Override
  public Optional<EventEntityIF> getByEventIdString(@NonNull String eventId) {
    return eventEntityService.findByEventIdString(eventId);
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
  public Optional<EventEntityIF> getEventById(@NonNull Long id) {
    return eventEntityService.getEventById(id);
  }

  @Override
  public List<EventEntityIF> getEventsByKind(@NonNull Kind kind) {
    return eventEntityService.getEventsByKind(kind);
  }

  @Override
  public void saveEventEntityOrDocument(@NonNull EventIF event) {
    eventEntityService.saveEventEntity(event);
  }

  @Override
  public List<EventEntityIF> getAll() {
    return eventEntityService.getAll();
  }

  @Override
  public Map<Kind, Map<?, ? extends EventIF>> getAllMappedByKind() {
    List<EventEntityIF> eventEntities = getAll();
//    TODO: fix missing generic    
    List<DeletionEntityIF> deletionEventEntities = getAllDeletionEventEntities();

    List<EventEntityIF> list = eventEntities.stream().filter(eventEntityIF ->
        deletionEventEntities.stream()
            .anyMatch(deletionEventEntityJpaIF ->
                deletionEventEntityJpaIF.getId().equals(eventEntityIF.getId()))).toList();

    Map<Kind, Map<Long, EventEntityIF>> collect = list.stream()
        .collect(
            Collectors.groupingBy(EventIF::getKind,
                Collectors.toMap(
                    EventEntityIF::getId,
                    Function.identity())));

    Map<Kind, Map<?, ? extends EventIF>> filteredDeletedEventsAsWildcards = collect.entrySet().stream().collect(
        Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue));

    log.debug("filteredDeletedEventsAsWildcards {}", filteredDeletedEventsAsWildcards);
    return filteredDeletedEventsAsWildcards;
  }

  //    TODO: fix missing generic  
  @Override
  public List<DeletionEntityIF> getAllDeletionEventEntities() {
    List<DeletionEntityIF> all = deletionEventEntityService.findAll();
    return all;
  }

  @Override
  public void deleteEventEntity(@NonNull Long id) {
    deletionEventEntityService.addDeletionEvent(id);
  }
}
