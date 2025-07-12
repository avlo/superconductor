package com.prosilion.superconductor.lib.redis.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.DeletionEventEntityIF;
import com.prosilion.superconductor.base.EventEntityIF;
import com.prosilion.superconductor.base.service.event.CacheIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
//@Component
public class RedisCache implements CacheIF {

  //  @Autowired
  public RedisCache(
//      @NonNull EventEntityService eventEntityService,
//      @NonNull DeletionEventEntityService deletionEventEntityService
  ) {
//    this.eventEntityService = eventEntityService;
//    this.deletionEventEntityService = deletionEventEntityService;
  }

  @Override
  public Map<Kind, Map<Long, GenericEventKindIF>> getAll() {
//    final List<DeletionEventEntityIF> allDeletionEventEntities = getAllDeletionEventEntities(); // do up front
//    Map<Kind, Map<Long, GenericEventKindIF>> returnedSet = getAllEventEntities().entrySet().stream()
//        .filter(
//            eventMap ->
//                eventMap.getValue().keySet().stream().noneMatch(eventId ->
//                    checkEventIdMatchesAnyDeletionEventEntityId(eventId, allDeletionEventEntities)))
//        .filter(kindMapEntry ->
//            !kindMapEntry.getKey().equals(Kind.CLIENT_AUTH))
//        .collect(
//            Collectors.toMap(Entry::getKey, Entry::getValue));
//    log.debug("returned events (per kind) after deletion event filtering and auth event filtering:\n  {}\n", returnedSet);
//    return returnedSet;
    return null;
  }

  @Override
  public Optional<EventEntityIF> getByEventIdString(@NonNull String eventId) {
//    return eventEntityService.findByEventIdString(eventId);
    return null;
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
  public void saveEventEntity(@NonNull GenericEventKindIF event) {
//    eventEntityService.saveEventEntity(event);
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
