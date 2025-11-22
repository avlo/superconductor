package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFormulaEventService extends AbstractCacheEventTagBaseEventService {
  public static final String NON_EXISTENT_EVENT_ID_S = "Event ID [%s] contains EventTag(s) referencing non-existent event ID(s): ";
  public static final String FORMATTED = "formula event found with matching author public key and identifier tag (UUID) but with different formula:\n  (db) [%s]\n    -vs- (incoming formula) [%s]\n";
  CacheServiceIF cacheServiceIF;

  public CacheFormulaEventService(@NonNull CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public void save(EventIF event) {
    Optional<FormulaEvent> dbFormulaEventWithMatchingAuthorPubkeyAndIdentifierTag = getFormulaEventByKindAndAuthorPublicKeyAndIdentifierTag(event);
    if (dbFormulaEventWithMatchingAuthorPubkeyAndIdentifierTag.isPresent()) {
      IdentifierTag incomingFormulaIdentifierTag = Filterable.getTypeSpecificTags(IdentifierTag.class, event).stream().findFirst().orElseThrow();
      IdentifierTag dbIdentifierTag = dbFormulaEventWithMatchingAuthorPubkeyAndIdentifierTag.get().getBadgeDefinitionAwardEvent().getIdentifierTag();
      if (dbIdentifierTag.equals(incomingFormulaIdentifierTag)) {
        String dbFormula = dbFormulaEventWithMatchingAuthorPubkeyAndIdentifierTag.get().getFormula();
        String incomingFormula = event.getContent();
        if (!dbFormula.equals(incomingFormula)) {
          throw new NostrException(
              String.format(FORMATTED, dbFormula, incomingFormula));
        }
        return; // implicit equals, does not require save
      }
    }

    super.save(event);
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

  private Optional<FormulaEvent> getFormulaEventByKindAndAuthorPublicKeyAndIdentifierTag(EventIF incomingFormulaEvent) {
    List<EventTag> eventTags = Filterable.getTypeSpecificTags(EventTag.class, incomingFormulaEvent);
    if (eventTags.size() > 1) {
      throw new NostrException("more than one identifier tag found");
    }

    if (eventTags.isEmpty()) {
      throw new NostrException("no identifier tag found");
    }

    Optional<GenericEventRecord> eventTagBadgeDefinitionAwardEvent = cacheServiceIF.getEventByEventId(eventTags.getFirst().getIdEvent());


//    Filterable.getTypeSpecificTags(IdentifierTag.class, eventTagBadgeDefinitionAwardEvent
    GenericEventRecord genericEventRecord = eventTagBadgeDefinitionAwardEvent.orElseThrow();
    IdentifierTag identifierTag = Filterable.getTypeSpecificTags(IdentifierTag.class, genericEventRecord).getFirst();

    Optional<? extends EventTagsMappedEventsIF> eventsByKindAndAuthorPublicKey = getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
        getKind(),
        incomingFormulaEvent.getPublicKey(),
        identifierTag).stream().findFirst();

    Optional<FormulaEvent> eventsByKindAndAuthorPublicKeyAndIdentifierTag1 = (Optional<FormulaEvent>) eventsByKindAndAuthorPublicKey;
    return eventsByKindAndAuthorPublicKeyAndIdentifierTag1;
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
