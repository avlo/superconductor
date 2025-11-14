package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionAwardEventService implements CacheEventTagBaseEventServiceIF {
  private final CacheServiceIF cacheServiceIF;

  public CacheBadgeDefinitionAwardEventService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public BadgeDefinitionAwardEvent save(@NonNull EventIF event) {
    GenericEventRecord genericEventRecordFromEntityIF = cacheServiceIF.save(event);
    BadgeDefinitionAwardEvent baseEventFromEntityIF = getBadgeDefinitionAwardEvent(genericEventRecordFromEntityIF);
    return baseEventFromEntityIF;
  }

  private BadgeDefinitionAwardEvent getBadgeDefinitionAwardEvent(GenericEventRecord genericEventRecord) {
    BadgeDefinitionAwardEvent baseEventFromEntityIF = new BadgeDefinitionAwardEvent(genericEventRecord);
    return baseEventFromEntityIF;
  }
  
  @Override
  public <T extends BaseEvent> T createBaseEventFromGenericRecord(GenericEventRecord genericEventRecord, Class<T> baseEventFromKind) {
    return null;
  }

  @Override
  public Optional<BadgeDefinitionAwardEvent> getEventByEventId(@NonNull String eventId) {
    Optional<GenericEventRecord> byEventIdString = cacheServiceIF.getEventByEventId(eventId);
    Optional<BadgeDefinitionAwardEvent> formulaEventOptional = byEventIdString.map(this::getBadgeDefinitionAwardEvent);
    return formulaEventOptional;
  }

  @Override
  public Optional<BadgeDefinitionAwardEvent> getEvent(@NonNull EventIF eventIF) {
    Optional<GenericEventRecord> byEventIdString = cacheServiceIF.getEventByEventId(eventIF.getId());
    Optional<BadgeDefinitionAwardEvent> formulaEventOptional = byEventIdString.map(this::getBadgeDefinitionAwardEvent);
    return formulaEventOptional;
  }

  @Override
  public List<BadgeDefinitionAwardEvent> getByKind(@NonNull Kind kind) {
    List<GenericEventRecord> eventsByKind = cacheServiceIF.getByKind(kind);
    List<BadgeDefinitionAwardEvent> collect = eventsByKind.stream().map(this::getBadgeDefinitionAwardEvent).toList();
    return collect;
  }

  @Override
  public List<BadgeDefinitionAwardEvent> getAll() {
    List<BadgeDefinitionAwardEvent> formulaEvents = cacheServiceIF.getAll().stream().map(this::getBadgeDefinitionAwardEvent).toList();
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
