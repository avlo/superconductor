package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionReputationEventService extends AbstractCacheEventTagBaseEventService {
  private final CacheFormulaEventService cacheFormulaEventService;

  public CacheBadgeDefinitionReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull @Qualifier("cacheFormulaEventService") CacheEventTagBaseEventServiceIF cacheFormulaEventService) {
    super(cacheServiceIF);
    this.cacheFormulaEventService = (CacheFormulaEventService) cacheFormulaEventService;
  }

  @Override
  public void save(@NonNull EventIF badgeDefinitionReputationEvent) {
    super.getEventTagMappedEvents(badgeDefinitionReputationEvent);
    super.save(badgeDefinitionReputationEvent);
  }

  @Override
  EventTagsMappedEventsIF populate(
      GenericEventRecord badgeDefinitionReputationEvent,
      List<GenericEventRecord> unpopulatedFormulaEvents) {

    List<FormulaEvent> populatedFormulaEvents = unpopulatedFormulaEvents.stream()
        .map(event ->
            cacheFormulaEventService.populate(
                event,
                getList(event))).toList();

    Function<EventTag, FormulaEvent> fxn = eventTag ->
        populatedFormulaEvents.stream().filter(genericEventRecord ->
                genericEventRecord.getId().equals(eventTag.getIdEvent()))
            .findFirst().orElseThrow();

    BadgeDefinitionReputationEvent eventGivenMappedEventTagEvents = createEventGivenMappedEventTagEvents(
        badgeDefinitionReputationEvent,
        BadgeDefinitionReputationEvent.class,
        fxn);

    return eventGivenMappedEventTagEvents;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEventByEventId(String eventId) {
    Optional<BadgeDefinitionReputationEvent> eventByEventId = (Optional<BadgeDefinitionReputationEvent>) super.getEventByEventId(eventId);
    return eventByEventId;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
