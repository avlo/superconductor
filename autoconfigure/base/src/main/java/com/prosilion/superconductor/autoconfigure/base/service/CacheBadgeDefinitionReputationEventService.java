package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionReputationEventService implements CacheEventTagBaseEventServiceIF {
  public static final String NON_EXISTENT_EVENT_ID_S = "Event ID [%s] contains EventTag(s) referencing non-existent event ID(s): ";
  private final CacheServiceIF cacheServiceIF;
  private final CacheFormulaEventService cacheFormulaEventService;

  public CacheBadgeDefinitionReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull @Qualifier("cacheFormulaEventService") CacheEventTagBaseEventServiceIF cacheFormulaEventService) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheFormulaEventService = (CacheFormulaEventService) cacheFormulaEventService;
  }

  @Override
  public BadgeDefinitionReputationEvent save(@NonNull EventIF event) {
    List<EventTag> eventTags = Filterable.getTypeSpecificTags(EventTag.class, event);
//    confirm eventTag(s) exist as DB events before saving event itself

    Set<String> foundDbEvents = eventTags.stream()
        .map(EventTag::getIdEvent)
        .map(cacheServiceIF::getEventByEventId)
        .flatMap(Optional::stream)
        .map(GenericEventRecord::getId).collect(Collectors.toSet());

    Set<String> missingEventIds = eventTags.stream().map(EventTag::getIdEvent).filter(eventTag -> !foundDbEvents.contains(eventTag)).collect(Collectors.toSet());

    if (!missingEventIds.isEmpty())
      throw new NostrException(
          Strings.concat(
              String.format(NON_EXISTENT_EVENT_ID_S, event.getId()),
              Strings.join(missingEventIds.stream().map(eventId -> String.format("[%s]", eventId)).toList(), ',')));

    GenericEventRecord genericEventRecordFromEntityIF = cacheServiceIF.save(event);
    BadgeDefinitionReputationEvent baseEventFromEntityIF = getBadgeDefinitionReputationEvent(genericEventRecordFromEntityIF);
    return baseEventFromEntityIF;
  }

  private BadgeDefinitionReputationEvent getBadgeDefinitionReputationEvent(GenericEventRecord genericEventRecord) {
    BadgeDefinitionReputationEvent baseEventFromEntityIF = createBaseEventFromGenericRecord(genericEventRecord, BadgeDefinitionReputationEvent.class);
    return baseEventFromEntityIF;
  }

  @Override
  public <T extends BaseEvent> T createBaseEventFromGenericRecord(GenericEventRecord genericEventRecord, Class<T> baseEventFromKind) {
    Set<FormulaEvent> collect = Filterable.getTypeSpecificTagsStream(EventTag.class, genericEventRecord)
        .map(EventTag::getIdEvent)
        .map(this::getAwardEventByEventId)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

    Function<EventTag, FormulaEvent> fxn = eventTag ->
        collect.stream().filter(formulaEvent ->
            formulaEvent.getId().equals(eventTag.getIdEvent())).findFirst().orElseThrow();

    return cacheServiceIF.createBaseEvent(genericEventRecord, baseEventFromKind, fxn);
  }

  public Optional<FormulaEvent> getAwardEventByEventId(@NonNull String eventId) {
    return cacheFormulaEventService.getEventByEventId(eventId);
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEventByEventId(@NonNull String eventId) {
    Optional<GenericEventRecord> byEventIdString = cacheServiceIF.getEventByEventId(eventId);
    Optional<BadgeDefinitionReputationEvent> formulaEventOptional = byEventIdString.map(this::getBadgeDefinitionReputationEvent);
    return formulaEventOptional;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull EventIF eventIF) {
    Optional<GenericEventRecord> byEventIdString = cacheServiceIF.getEventByEventId(eventIF.getId());
    Optional<BadgeDefinitionReputationEvent> formulaEventOptional = byEventIdString.map(this::getBadgeDefinitionReputationEvent);
    return formulaEventOptional;
  }

  @Override
  public List<BadgeDefinitionReputationEvent> getByKind(@NonNull Kind kind) {
    List<GenericEventRecord> eventsByKind = cacheServiceIF.getByKind(kind);
    List<BadgeDefinitionReputationEvent> collect = eventsByKind.stream().map(this::getBadgeDefinitionReputationEvent).toList();
    return collect;
  }

  @Override
  public List<BadgeDefinitionReputationEvent> getAll() {
    List<BadgeDefinitionReputationEvent> formulaEvents = cacheServiceIF.getAll().stream().map(this::getBadgeDefinitionReputationEvent).toList();
    return formulaEvents;
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    cacheServiceIF.deleteEvent(eventIF);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
