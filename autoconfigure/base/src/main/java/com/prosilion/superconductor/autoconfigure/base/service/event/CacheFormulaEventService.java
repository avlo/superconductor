package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFormulaEventService implements CacheFormulaEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG = "FormulaEvent [%s] is missing required AddressTag";
  public static final String NON_EXISTENT_BADGE_DEFINITION_AWARD_EVENT_S = "FormulaEvent [%s] contains AddressTag referencing non-existent BadgeDefinitionGenericEvent";
  public static final String FORMATTED = "formula event found with matching author public key and identifier tag (UUID) but with different formula:\n  (db) [%s]\n    -vs- (incoming formula) [%s]\n";
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;

  public CacheFormulaEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
  }

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String eventId, @NonNull String url) {
    Optional<GenericEventRecord> unpopulatedFormulaEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedFormulaEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedFormulaEvent.get()));
  }

  @Override
  public FormulaEvent materialize(@NonNull EventIF incomingFormulaEvent) {
    log.debug("processing incoming EventIF as FORMULA EVENT: [{}]", incomingFormulaEvent);
    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent = getBadgeDefinitionGenericEvent(incomingFormulaEvent.asGenericEventRecord());

    FormulaEvent formulaEvent = new FormulaEvent(
        incomingFormulaEvent.asGenericEventRecord(),
        addressTag -> badgeDefinitionGenericEvent);

// check for existing formula event using pubkey and identifier tag
    Optional<GenericEventRecord> dbOptionalFormulaEvent = cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
        formulaEvent.getKind(),
        formulaEvent.getPublicKey(),
        formulaEvent.getIdentifierTag()).stream().findFirst();

// 	if existing formula event not found:
    if (dbOptionalFormulaEvent.isEmpty()) {
//      log.debug("saving new FormulaEvent {}...", incomingFormulaEvent);
//      cacheServiceIF.save(incomingFormulaEvent);
//      log.debug("...done");
      return formulaEvent;
    }

// existing formula event was found    
// check non-conflicting formula content
    IdentifierTag incomingFormulaIdentifierTag = formulaEvent.getIdentifierTag();
    IdentifierTag dbIdentifierTag =
        Filterable.getTypeSpecificTagsStream(IdentifierTag.class, dbOptionalFormulaEvent.get()).findFirst().orElseThrow();

//    identifier tag different, ok to save incomingFormulaEvent
    if (!dbIdentifierTag.equals(incomingFormulaIdentifierTag)) {
//      log.debug("saving new FormulaEvent {}...", incomingFormulaEvent);
//      cacheServiceIF.save(incomingFormulaEvent);
//      log.debug("...done");
      return formulaEvent;
    }

    String dbFormula = dbOptionalFormulaEvent.get().content();
    String incomingFormula = formulaEvent.getContent();
// 		if formula content different
// 			throw exception
    if (!dbFormula.equals(incomingFormula)) {
      throw new NostrException(
          String.format(FORMATTED, dbFormula, incomingFormula));
    }

    log.debug("Identical FormulaEvent already exists, return it");
    return formulaEvent;
  }

  private BadgeDefinitionGenericEvent getBadgeDefinitionGenericEvent(@NonNull GenericEventRecord genericEventRecord) {
    AddressTag firstAddressTag = Filterable.getTypeSpecificTagsStream(AddressTag.class, genericEventRecord)
        .findFirst().orElseThrow(() ->
            new NostrException(
                String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord)));

    GenericEventRecord firstAddressTagAsEvent = cacheDereferenceAddressTagServiceIF.getEvent(firstAddressTag).orElseThrow(() ->
        new NostrException(
            String.format(NON_EXISTENT_BADGE_DEFINITION_AWARD_EVENT_S, genericEventRecord)));

    BadgeDefinitionGenericEvent addressTagNowBadgeDefinitionGenericEvent = new BadgeDefinitionGenericEvent(firstAddressTagAsEvent);

    return addressTagNowBadgeDefinitionGenericEvent;
  }

//  @Override
//  public List<FormulaEvent> getFormulaEventsGivenAssociatedBadgeDefinitionGenericEvent(@NonNull BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
//    List<FormulaEvent> formulaEvents = cacheServiceIF.getByKind(
//            Kind.ARBITRARY_CUSTOM_APP_DATA).stream()
//        .map(ger ->
//            new FormulaEvent(ger, aTag ->
//                getBadgeDefinitionGenericEvent(ger)))
//        .filter(formulaEvent ->
//            formulaEvent.getBadgeDefinitionGenericEvent().equals(badgeDefinitionGenericEvent))
//        .toList();
//
//    return formulaEvents;
//  }

  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
