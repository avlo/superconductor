package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.DeletionEntityIF;
import com.prosilion.superconductor.lib.jpa.dto.deletion.DeletionEventEntityJpaIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
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
    return jpaEventEntityService.findByEventIdString(eventId);
  }

  @Override
  public Optional<EventEntityIF> getEventByUid(@NonNull Long id) {
    return jpaEventEntityService.getEventByUid(id);
  }

  public void deleteEventEntity(@NonNull EventIF eventIF) {
    Function<EventEntityIF, Long> getUid = EventEntityIF::getUid;
    Consumer<Long> addDeletionEvent = jpaDeletionEventEntityService::addDeletionEvent;
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
  public List<EventEntityIF> getByKind(@NonNull Kind kind) {
    return jpaEventEntityService.getEventsByKind(kind);
  }

  @Override
  public Long save(@NonNull EventIF event) {
    return jpaEventEntityService.saveEventEntity(event);
  }


  public List<EventEntityIF> getAll() {
    List<EventEntityIF> all = jpaEventEntityService.getAll();
    List<DeletionEventEntityJpaIF> deletionEventEntities = getAllDeletionJpaEventEntities();

    return all.stream()
        .filter(eventEntityIF ->
            !deletionEventEntities.stream()
                .map(DeletionEntityIF::getId)
                .toList()
                .contains(eventEntityIF.getUid())).toList();
  }

  public List<DeletionEventEntityJpaIF> getAllDeletionJpaEventEntities() {
    return jpaDeletionEventEntityService.findAll();
  }
}
