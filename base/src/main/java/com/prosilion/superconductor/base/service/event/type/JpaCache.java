package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.entity.EventEntity;
import com.prosilion.superconductor.base.entity.join.deletion.DeletionEventEntity;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
// TODO: caching currently non-critical although ready for implementation anytime
public class JpaCache implements CacheIF {
  private final EventEntityService eventEntityService;
  private final DeletionEventEntityService deletionEventEntityService;

  @Autowired
  public JpaCache(
      @NonNull EventEntityService eventEntityService,
      @NonNull DeletionEventEntityService deletionEventEntityService) {
    this.eventEntityService = eventEntityService;
    this.deletionEventEntityService = deletionEventEntityService;
  }

  @Override
  public Map<Kind, Map<Long, GenericEventKindIF>> getAll() {
    final List<DeletionEventEntity> allDeletionEventEntities = getAllDeletionEventEntities(); // do up front
    Map<Kind, Map<Long, GenericEventKindIF>> returnedSet = getAllEventEntities().entrySet().stream()
        .filter(
            eventMap ->
                eventMap.getValue().keySet().stream().noneMatch(eventId ->
                    checkEventIdMatchesAnyDeletionEventEntityId(eventId, allDeletionEventEntities)))
        .filter(kindMapEntry ->
            !kindMapEntry.getKey().equals(Kind.CLIENT_AUTH))
        .collect(
            Collectors.toMap(Entry::getKey, Entry::getValue));
    log.debug("returned events (per kind) after deletion event filtering and auth event filtering:\n  {}\n", returnedSet);
    return returnedSet;
  }

  @Override
  public Optional<EventEntity> getByEventIdString(@NonNull String eventId) {
    return eventEntityService.findByEventIdString(eventId);
  }

  @Override
  public Optional<EventEntity> getByMatchingAddressableTags(@NonNull String eventId) {
    return eventEntityService.findByEventIdString(eventId);
  }

  @Override
  public GenericEventKindIF getEventById(@NonNull Long id) {
    return eventEntityService.getEventById(id);
  }

  @Override
  public void saveEventEntity(@NonNull GenericEventKindIF event) {
    eventEntityService.saveEventEntity(event);
  }

  @Override
  public void deleteEventEntity(@NonNull EventEntity event) {
    eventEntityService.deleteEventEntity(event);
  }

  //  TODO: event entity cache-location candidate
  private Map<Kind, Map<Long, GenericEventKindIF>> getAllEventEntities() {
    return eventEntityService.getAll();
  }

  //  TODO: deletionEvent entity cache-location candidate
  private List<DeletionEventEntity> getAllDeletionEventEntities() {
    return deletionEventEntityService.findAll();
  }
}
