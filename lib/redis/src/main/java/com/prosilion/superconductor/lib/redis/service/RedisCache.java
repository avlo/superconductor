package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.DeletionEventEntityIF;
import com.prosilion.superconductor.base.EventIF;
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
  public Optional<GenericEventKindIF> getByEventIdString(@NonNull String eventId) {
    return eventDocumentService.findByEventIdString(eventId);
  }

  @Override
  public List<GenericEventKindIF> getEventsByKind(@NonNull Kind kind) {
    return eventDocumentService.getEventsByKind(kind);
  }
  
  @Override
  public void saveEventEntityOrDocument(@NonNull GenericEventKindIF event) {
    eventDocumentService.saveEventDocument(event);
  }

  @Override
  public void deleteEventEntity(@NonNull EventIF eventIF) {
//    eventEntityService.deleteEventEntity(event);
  }

  @Override
  public Map<Kind, Map<String, GenericEventKindIF>> getAllEventEntities() {
    return eventDocumentService.getAll();
  }

  @Override
  public List<DeletionEventEntityIF> getAllDeletionEventEntities() {
//    return deletionEventEntityService.findAll();
    return null;
  }
}
