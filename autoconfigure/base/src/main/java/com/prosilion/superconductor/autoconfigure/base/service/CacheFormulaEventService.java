package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFormulaEventService extends AbstractCacheEventTagBaseEventService {
  public static final String NON_EXISTENT_EVENT_ID_S = "Event ID [%s] contains EventTag(s) referencing non-existent event ID(s): ";

  public CacheFormulaEventService(@NonNull CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
  }

  @Override
  public void save(@NonNull EventIF formulaEvent) {
    super.getEventTagMappedEvents(formulaEvent);
    super.save(formulaEvent);
  }

  @Override
  EventTagsMappedEventsIF populate(
      GenericEventRecord formulaEvent,
      List<GenericEventRecord> badgeDefinitionAwardEvents) {
    EventTagsMappedEventsIF baseEventFromEntityIF =
        createEventGivenMappedEventTagEvents(
            formulaEvent,
            FormulaEvent.class,
            badgeDefinitionAwardEvents);
    return baseEventFromEntityIF;
  }

//  @Override
//  public BadgeDefinitionAwardEvent createEventTagMappedEvent(GenericEventRecord genericEventRecord) {
//    BadgeDefinitionAwardEvent baseEvent = super.createBaseEvent(
//        genericEventRecord,
//        BadgeDefinitionAwardEvent.class);
//    return baseEvent;
//  }


  @Override
  public Optional<FormulaEvent> getEventByEventId(String eventId) {
    Optional<FormulaEvent> eventByEventId = (Optional<FormulaEvent>) super.getEventByEventId(eventId);
    return eventByEventId;
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
