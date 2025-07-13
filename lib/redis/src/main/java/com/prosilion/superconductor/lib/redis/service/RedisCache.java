package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.DeletionEventEntityIF;
import com.prosilion.superconductor.base.EventEntityIF;
import com.prosilion.superconductor.base.service.event.CacheIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisCache implements CacheIF {
  private final EventDocumentService eventDocumentService;

  @Autowired
  public RedisCache(
      @NonNull EventDocumentService eventDocumentService) {
    this.eventDocumentService = eventDocumentService;
  }

  @Override
  public Map<Kind, Map<Long, GenericEventKindIF>> getAll() {
    return null;
  }

  @Override
  public Optional<EventEntityIF> getByEventIdString(@NonNull String eventId) {
    return eventDocumentService.findByEventIdString(eventId);
  }

  @Override
  public Optional<EventEntityIF> getByMatchingAddressableTags(@NonNull String eventId) {
//    return eventEntityService.findByEventIdString(eventId);
    return null;
  }

  @Override
  public GenericEventKindIF getEventById(@NonNull Long id) {
//    return eventEntityService.getEventById(id);
    return null;
  }

  @Override
  public void saveEventEntityOrDocument(@NonNull GenericEventKindIF event) {
    eventDocumentService.saveEventDocument(event);
  }

  @Override
  public void deleteEventEntity(@NonNull EventEntityIF event) {
//    eventEntityService.deleteEventEntity(event);
  }

  //  TODO: event entity cache-location candidate
  private Map<Kind, Map<Long, GenericEventKindIF>> getAllEventEntities() {
//    return eventEntityService.getAll();
    return null;
  }

  //  TODO: deletionEvent entity cache-location candidate
  private List<DeletionEventEntityIF> getAllDeletionEventEntities() {
//    return deletionEventEntityService.findAll();
    return null;
  }
}
