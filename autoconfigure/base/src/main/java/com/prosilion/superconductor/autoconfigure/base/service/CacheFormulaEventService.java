package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
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
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFormulaEventService implements CacheEventTagBaseEventServiceIF {
  public static final String NON_EXISTENT_EVENT_ID_S = "Event ID [%s] contains EventTag(s) referencing non-existent event ID(s): ";
  private final CacheServiceIF cacheServiceIF;

  public CacheFormulaEventService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public FormulaEvent save(@NonNull BaseEvent event) {
    List<EventTag> eventTags = event.getTypeSpecificTags(EventTag.class);
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
    FormulaEvent baseEventFromEntityIF = getFormulaEvent(genericEventRecordFromEntityIF);
    return baseEventFromEntityIF;
  }

  @Override
  public Optional<FormulaEvent> getEventByEventId(@NonNull String eventId) {
    Optional<GenericEventRecord> byEventIdString = cacheServiceIF.getEventByEventId(eventId);
    Optional<FormulaEvent> formulaEventOptional = byEventIdString.map(this::getFormulaEvent);
    return formulaEventOptional;
  }

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull EventIF eventIF) {
    Optional<GenericEventRecord> byEventIdString = cacheServiceIF.getEventByEventId(eventIF.getId());
    Optional<FormulaEvent> formulaEventOptional = byEventIdString.map(this::getFormulaEvent);
    return formulaEventOptional;
  }

  @Override
  public List<FormulaEvent> getByKind(@NonNull Kind kind) {
    List<GenericEventRecord> eventsByKind = cacheServiceIF.getByKind(kind);
    List<FormulaEvent> collect = eventsByKind.stream().map(this::getFormulaEvent).toList();
    return collect;
  }

  private FormulaEvent getFormulaEvent(GenericEventRecord genericEventRecordFromEntityIF) {
    FormulaEvent baseEventFromEntityIF = createBaseEventFromEntityIF(genericEventRecordFromEntityIF, FormulaEvent.class);
    return baseEventFromEntityIF;
  }

  @Override
  public <T extends BaseEvent> T createBaseEventFromEntityIF(GenericEventRecord genericEventRecord, Class<T> baseEventFromKind) {

    Set<FormulaEvent> collect = Filterable.getTypeSpecificTagsStream(EventTag.class, genericEventRecord)
        .map(EventTag::getIdEvent)
        .map(this::getEventByEventId)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

    Function<EventTag, FormulaEvent> fxn = eventTag ->
        collect.stream().filter(formulaEvent ->
            formulaEvent.getId().equals(eventTag.getIdEvent())).findFirst().orElseThrow();
    
    return cacheServiceIF.createBaseEvent(genericEventRecord, baseEventFromKind, fxn);
  }

  @Override
  public List<FormulaEvent> getAll() {
    List<FormulaEvent> formulaEvents = cacheServiceIF.getAll().stream().map(this::getFormulaEvent).toList();
    return formulaEvents;
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    cacheServiceIF.deleteEvent(eventIF);
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
