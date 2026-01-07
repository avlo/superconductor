package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFormulaEventService implements CacheFormulaEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG = "FormulaEvent [%s] is missing required AddressTag";
  public static final String NON_EXISTENT_BADGE_DEFINITION_AWARD_EVENT_S = "FormulaEvent [%s] contains AddressTag referencing non-existent BadgeDefinitionAwardEvent";
  public static final String FORMATTED = "formula event found with matching author public key and identifier tag (UUID) but with different formula:\n  (db) [%s]\n    -vs- (incoming formula) [%s]\n";
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;

  public CacheFormulaEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
  }

  @Override
  public FormulaEvent reconstruct(@NonNull FormulaEvent incomingFormulaEvent) {
// check formula event AddressTag (badge definition award) existence
    AddressTag incomingFormulaEventAddressTag = incomingFormulaEvent.getContainedAddressableEvents().stream().findFirst().orElseThrow(() ->
        new NostrException(
            String.format(NON_EXISTENT_ADDRESS_TAG, incomingFormulaEvent)));

//  if badge definition award not found
// 		throw exception
    cacheDereferenceAddressTagServiceIF.getEvent(incomingFormulaEventAddressTag).orElseThrow(() ->
        new NostrException(
            String.format(NON_EXISTENT_BADGE_DEFINITION_AWARD_EVENT_S, incomingFormulaEvent)));

// check for existing formula event using pubkey and identifier tag
    Optional<GenericEventRecord> dbOptionalFormulaEvent = cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
        incomingFormulaEvent.getKind(),
        incomingFormulaEvent.getPublicKey(),
        incomingFormulaEvent.getIdentifierTag()).stream().findFirst();

// 	if existing formula event not found:
    if (dbOptionalFormulaEvent.isEmpty()) {
//      log.debug("saving new FormulaEvent {}...", incomingFormulaEvent);
//      cacheServiceIF.save(incomingFormulaEvent);
//      log.debug("...done");
      return incomingFormulaEvent;
    }

// existing formula event was found    
// check non-conflicting formula content
    IdentifierTag incomingFormulaIdentifierTag = incomingFormulaEvent.getIdentifierTag();
    FormulaEvent existingFormulaEvent = getEvent(dbOptionalFormulaEvent.get().getId()).orElseThrow();
    IdentifierTag dbIdentifierTag = existingFormulaEvent.getBadgeDefinitionAwardEvent().getIdentifierTag();

//    identifier tag different, ok to save incomingFormulaEvent
    if (!dbIdentifierTag.equals(incomingFormulaIdentifierTag)) {
//      log.debug("saving new FormulaEvent {}...", incomingFormulaEvent);
//      cacheServiceIF.save(incomingFormulaEvent);
//      log.debug("...done");
      return incomingFormulaEvent;
    }

    String dbFormula = existingFormulaEvent.getFormula();
    String incomingFormula = incomingFormulaEvent.getContent();
// 		if formula content different
// 			throw exception
    if (!dbFormula.equals(incomingFormula)) {
      throw new NostrException(
          String.format(FORMATTED, dbFormula, incomingFormula));
    }

    log.debug("Identical FormulaEvent already exists, return it");
    return incomingFormulaEvent;
  }

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String eventId) {
    Optional<GenericEventRecord> unpopulatedFormulaEvent = cacheServiceIF.getEventByEventId(eventId);
    if (unpopulatedFormulaEvent.isEmpty())
      return Optional.empty();

    return Optional.of(cacheDereferenceAddressTagServiceIF.createTypedFxnEvent(
        unpopulatedFormulaEvent.orElseThrow(),
        FormulaEvent.class,
        addressTag ->
            getBadgeDefinitionAwardEvent(unpopulatedFormulaEvent.get())));
  }

  @Override
  public BadgeDefinitionAwardEvent getBadgeDefinitionAwardEvent(@NonNull GenericEventRecord genericEventRecord) {
    AddressTag firstAddressTag = Filterable.getTypeSpecificTagsStream(AddressTag.class, genericEventRecord)
        .findFirst().orElseThrow(() ->
            new NostrException(
                String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord)));

    GenericEventRecord firstAddressTagAsEvent = cacheDereferenceAddressTagServiceIF.getEvent(firstAddressTag).orElseThrow(() ->
        new NostrException(
            String.format(NON_EXISTENT_BADGE_DEFINITION_AWARD_EVENT_S, genericEventRecord)));

    BadgeDefinitionAwardEvent addressTagNowBadgeDefinitionAwardEvent =
        cacheServiceIF.createTypedSimpleEvent(
            firstAddressTagAsEvent,
            BadgeDefinitionAwardEvent.class);
    return addressTagNowBadgeDefinitionAwardEvent;
  }

  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
