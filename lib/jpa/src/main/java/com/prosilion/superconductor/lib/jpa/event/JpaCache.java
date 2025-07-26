package com.prosilion.superconductor.lib.jpa.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.DeletionEventEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import com.prosilion.superconductor.lib.jpa.service.DeletionEventEntityService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  public Optional<GenericEventKindIF> getByEventIdString(@NonNull String eventId) {
    return eventEntityService.findByEventIdString(eventId);
  }

  @Override
  public Optional<EventEntityIF> getEventByIdStringAsEventEntityIF(@NonNull String eventId) {
    return eventEntityService.getEventByIdStringAsEventEntityIF(eventId);
  }

  @Override
  public Optional<GenericEventKindIF> getEventById(@NonNull Long id) {
    return eventEntityService.getEventById(id);
  }

  @Override
  public List<GenericEventKindIF> getEventsByKind(@NonNull Kind kind) {
    return eventEntityService.getEventsByKind(kind);
  }

  @Override
  public void saveEventEntityOrDocument(@NonNull GenericEventKindIF event) {
    eventEntityService.saveEventEntity(event);
  }

  @Override
  public Map<Kind, Map<Long, GenericEventKindIF>> getAllEventEntities() {
    return eventEntityService.getAll();
  }

  @Override
  public List<DeletionEventEntityIF> getAllDeletionEventEntities() {
    return deletionEventEntityService.findAll();
  }

  @Override
  public void deleteEventEntity(@NonNull Long id) {
    deletionEventEntityService.addDeletionEvent(id);
  }
}
