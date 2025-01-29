package com.prosilion.superconductor.service.event.type;

import com.prosilion.superconductor.entity.EventEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
// TODO: caching currently non-critical although ready for implementation anytime
public class RedisCache<T extends GenericEvent> {
  private final EventEntityService<T> eventEntityService;
  private final DeletionEventEntityService deletionEventEntityService;

  @Autowired
  public RedisCache(@NonNull
      EventEntityService<T> eventEntityService,
      DeletionEventEntityService deletionEventEntityService) {
    this.eventEntityService = eventEntityService;
    this.deletionEventEntityService = deletionEventEntityService;
  }

  public Map<Kind, Map<Long, T>> getAll() {
    Map<Kind, Map<Long, T>> returnedSet = getAllEventEntities().entrySet().stream()
        .filter(
            kindMapEntry ->
                kindMapEntry.getValue().keySet().stream().noneMatch(
                    getAllDeletionEventEntities()::contains
                ))
        .filter(kindMapEntry ->
            !kindMapEntry.getKey().equals(Kind.CLIENT_AUTH))
        .collect(
            Collectors.toMap(Entry::getKey, Entry::getValue));
    log.debug("returned events (per kind) after deletion event filtering and auth event filtering:\n  {}\n", returnedSet);
    return returnedSet;
  }


  protected Optional<EventEntity> getByEventIdString(@NonNull String eventId) {
    return eventEntityService.findByEventIdString(eventId);
  }

  protected Optional<EventEntity> getByMatchingAddressableTags(@NonNull String eventId) {
    return eventEntityService.findByEventIdString(eventId);
  }

  protected T getEventById(@NonNull Long id) {
    return eventEntityService.getEventById(id);
  }

  public void saveEventEntity(@NonNull GenericEvent event) {
    eventEntityService.saveEventEntity(event);
  }

  protected void deleteEventEntity(@NonNull EventEntity event) {
    eventEntityService.deleteEventEntity(event);
  }

  //  TODO: event entity cache-location candidate
  private Map<Kind, Map<Long, T>> getAllEventEntities() {
    return eventEntityService.getAll();
  }

  //  TODO: deletionEvent entiy cache-location candidate
  private List<Long> getAllDeletionEventEntities() {
    return deletionEventEntityService.findAll();
  }
}
