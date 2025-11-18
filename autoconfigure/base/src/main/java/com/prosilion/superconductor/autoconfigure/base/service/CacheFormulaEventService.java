package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFormulaEventService extends AbstractCacheEventTagBaseEventService {
  public static final String NON_EXISTENT_EVENT_ID_S = "Event ID [%s] contains EventTag(s) referencing non-existent event ID(s): ";

  public CacheFormulaEventService(@NonNull CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
  }

  @Override
  public FormulaEvent populate(
      GenericEventRecord formulaEvent,
      List<GenericEventRecord> unpopulatedBadgeDefinitionAwardEvents) {

    Function<EventTag, BadgeDefinitionAwardEvent> fxn = eventTag ->
        createEventTagMappedEvent(unpopulatedBadgeDefinitionAwardEvents.stream().filter(genericEventRecord ->
                genericEventRecord.getId().equals(eventTag.getIdEvent()))
            .findFirst().orElseThrow());

    return createEventGivenMappedEventTagEvents(
        formulaEvent,
        FormulaEvent.class,
        fxn);
  }

  private BadgeDefinitionAwardEvent createEventTagMappedEvent(GenericEventRecord genericEventRecord) {
    return super.createBaseEvent(
        genericEventRecord,
        BadgeDefinitionAwardEvent.class);
  }

  public Optional<FormulaEvent> getFormulaEvent(String eventId) {
    return (Optional<FormulaEvent>) super.getEventByEventId(eventId);
  }

  public List<FormulaEvent> getFormulaEvents(@NonNull Kind kind) {
    return (List<FormulaEvent>) super.getByKind(kind);
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
