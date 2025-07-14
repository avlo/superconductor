package com.prosilion.superconductor.lib.jpa.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.DeletionEventEntityIF;
import com.prosilion.superconductor.base.EventIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import com.prosilion.superconductor.lib.jpa.service.DeletionEventEntityService;

@Slf4j
//@Component
public class JpaCache implements JpaCacheIF {
  private final EventEntityService eventEntityService;
  private final DeletionEventEntityService deletionEventEntityService;

  //  @Autowired
  public JpaCache(
      @NonNull EventEntityService eventEntityService,
      @NonNull DeletionEventEntityService deletionEventEntityService) {
    this.eventEntityService = eventEntityService;
    this.deletionEventEntityService = deletionEventEntityService;
  }

  @Override
  public Optional<EventIF> getByEventIdString(@NonNull String eventId) {
    return eventEntityService.findByEventIdString(eventId)
        .map(EventIF::convertEntityToDto);
  }

  @Override
  public Optional<EventIF> getByMatchingAddressableTags(@NonNull String eventId) {
//    return eventEntityService.findByEventIdString(eventId);
    return null;
  }

  @Override
  public GenericEventKindIF getEventById(@NonNull Long id) {
    return eventEntityService.getEventById(id);
  }

  @Override
  public void saveEventEntityOrDocument(@NonNull GenericEventKindIF event) {
    eventEntityService.saveEventEntity(event);
  }

  @Override
  public void deleteEventEntity(@NonNull EventIF event) {
    eventEntityService.deleteEventEntity((EventEntityIF) event);
  }

  @Override
  public Map<Kind, Map<Long, GenericEventKindIF>> getAllEventEntities() {
    return eventEntityService.getAll();
  }

  @Override
  public List<DeletionEventEntityIF> getAllDeletionEventEntities() {
    return deletionEventEntityService.findAll();
  }
}
