package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.CacheFormulaEventServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFormulaEventService implements CacheFormulaEventServiceIF {
  private final JpaCacheServiceIF jpaCacheServiceIF;

  public CacheFormulaEventService(@NonNull JpaCacheServiceIF jpaCacheServiceIF) {
    this.jpaCacheServiceIF = jpaCacheServiceIF;
  }

  @Override
  public FormulaEvent save(@NonNull EventIF event) {
    GenericEventRecord genericEventRecordFromEntityIF = jpaCacheServiceIF.save(event);
    FormulaEvent baseEventFromEntityIF = getFormulaEvent(genericEventRecordFromEntityIF);
    return baseEventFromEntityIF;
  }

  @Override
  public Optional<FormulaEvent> getEventByEventId(@NonNull String eventId) {
    Optional<GenericEventRecord> byEventIdString = jpaCacheServiceIF.getEventByEventId(eventId);
    Optional<FormulaEvent> formulaEventOptional = byEventIdString.map(this::getFormulaEvent);
    return formulaEventOptional;
  }

  @Override
  public Optional<FormulaEvent> getEventByUid(@NonNull Long id) {
    Optional<GenericEventRecord> byEventIdString = jpaCacheServiceIF.getEventByUid(id);
    Optional<FormulaEvent> formulaEventOptional = byEventIdString.map(this::getFormulaEvent);
    return formulaEventOptional;
  }

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull EventIF eventIF) {
    Optional<GenericEventRecord> byEventIdString = jpaCacheServiceIF.getEventByEventId(eventIF.getId());
    Optional<FormulaEvent> formulaEventOptional = byEventIdString.map(this::getFormulaEvent);
    return formulaEventOptional;
  }

  @Override
  public List<FormulaEvent> getByKind(@NonNull Kind kind) {
    List<GenericEventRecord> eventsByKind = jpaCacheServiceIF.getByKind(kind);
    List<FormulaEvent> collect = eventsByKind.stream().map(this::getFormulaEvent).toList();
    return collect;
  }

  private FormulaEvent getFormulaEvent(GenericEventRecord genericEventRecordFromEntityIF) {
    FormulaEvent baseEventFromEntityIF = createBaseEventFromEntityIF(genericEventRecordFromEntityIF, FormulaEvent.class);
    return baseEventFromEntityIF;
  }

  public FormulaEvent createBaseEventFromEntityIF(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<FormulaEvent> baseEventFromKind) {
    return jpaCacheServiceIF.createBaseEvent(genericEventRecord, baseEventFromKind);
  }

  @Override
  public List<FormulaEvent> getAll() {
    List<FormulaEvent> formulaEvents = jpaCacheServiceIF.getAll().stream().map(this::getFormulaEvent).toList();
    return formulaEvents;
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    jpaCacheServiceIF.deleteEvent(eventIF);
  }

  @Override
  public List<Long> getAllDeletionEventIds() {
    List<Long> all = jpaCacheServiceIF.getAllDeletionEventIds();
    return all;
  }
}
