package com.prosilion.superconductor.service.event;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
// TODO: caching currently non-critical although ready for implementation anytime
public class RedisCache<T extends GenericEvent> {
  private final EventEntityService<T> eventEntityService;

  @Autowired
  public RedisCache(EventEntityService<T> eventEntityService) {
    this.eventEntityService = eventEntityService;
  }

  public Map<Kind, Map<Long, T>> getAll() {
    return eventEntityService.getAll();
  }

  protected void saveEventEntity(@NonNull GenericEvent event) {
    eventEntityService.saveEventEntity(event);
  }
}