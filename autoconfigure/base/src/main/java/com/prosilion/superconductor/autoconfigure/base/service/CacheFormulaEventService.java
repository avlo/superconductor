package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.service.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.CacheReferencedAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFormulaEventService implements CacheFormulaEventServiceIF {
  public static final String NON_EXISTENT_EVENT_ID_S = "Event ID [%s] contains EventTag(s) referencing non-existent event ID(s): ";
  public static final String FORMATTED = "formula event found with matching author public key and identifier tag (UUID) but with different formula:\n  (db) [%s]\n    -vs- (incoming formula) [%s]\n";

  CacheServiceIF cacheServiceIF;
  CacheReferencedAddressTagServiceIF cacheReferencedAddressTagServiceIF;

  public CacheFormulaEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheReferencedAddressTagServiceIF cacheReferencedAddressTagServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheReferencedAddressTagServiceIF = cacheReferencedAddressTagServiceIF;
  }

  @Override
  public void save(FormulaEvent incomingFormulaEvent) {
    Optional<GenericEventRecord> optionalFormulaEvent = cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
        incomingFormulaEvent.getKind(),
        incomingFormulaEvent.getPublicKey(),
        incomingFormulaEvent.getIdentifierTag()).stream().findFirst();

    if (optionalFormulaEvent.isEmpty()) {
      log.debug("saving new FormulaEvent {}...", incomingFormulaEvent);
      cacheServiceIF.save(incomingFormulaEvent);
      log.debug("...done");
      return;
    }

    FormulaEvent existingFormulaEvent = getEvent(optionalFormulaEvent.get().getId()).orElseThrow();
    if (existingFormulaEvent.equals(incomingFormulaEvent)) {
      log.debug("incoming FormulaEvent {} matches existing FormulaEvent {}...", incomingFormulaEvent, existingFormulaEvent);
      return;
    }

    throw new NostrException(
        String.format("Incoming FormulaEvent [%s] formula clashes with pre-existing FormulaEvent [%s] formula having same Kind, PublicKey, Uuid", incomingFormulaEvent, existingFormulaEvent));
  }

//  private BadgeDefinitionAwardEvent getBadgeDefinitionAwardEventViaAddressTag(EventIF incomingFormulaEvent) {
//    List<AddressTag> addressTags = Filterable.getTypeSpecificTags(AddressTag.class, incomingFormulaEvent);
//    if (addressTags.size() > 1) {
//      throw new NostrException(
//          String.format("FormulaEvent contains more than one address tag: %s", addressTags));
//    }
//
//    if (addressTags.isEmpty()) {
//      throw new NostrException("FormulaEvent is missing an address tag");
//    }
//
//    AddressTag validAddressTag = addressTags.getFirst();
//
//    Optional<GenericEventRecord> badgeDefinitionAwardEvent = cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
//        validAddressTag.getKind(),
//        validAddressTag.getPublicKey(),
//        validAddressTag.getIdentifierTag()).stream().findFirst();
//
//    GenericEventRecord genericEventRecord = badgeDefinitionAwardEvent.orElseThrow();
//
//    BadgeDefinitionAwardEvent addressTagNowBadgeDefinitionAwardEvent =
//        cacheServiceIF.createTypedSimpleEvent(
//            genericEventRecord,
//            BadgeDefinitionAwardEvent.class);
//
//    return addressTagNowBadgeDefinitionAwardEvent;
//  }

  @Override
  public Optional<FormulaEvent> getEvent(String eventId) {
    Optional<GenericEventRecord> unpopulatedFormulaEvent = cacheServiceIF.getEventByEventId(eventId);
    if (unpopulatedFormulaEvent.isEmpty())
      return Optional.empty();

    BadgeDefinitionAwardEvent addressTagNowBadgeDefinitionAwardEvent =
        getBadgeDefinitionAwardEvent(
            unpopulatedFormulaEvent.get());

    Function<AddressTag, BadgeDefinitionAwardEvent> fxn = addressTag ->
        addressTagNowBadgeDefinitionAwardEvent;

    FormulaEvent formulaEvent = cacheReferencedAddressTagServiceIF.createTypedFxnEvent(
        unpopulatedFormulaEvent.orElseThrow(),
        FormulaEvent.class,
        fxn);

    return Optional.of(formulaEvent);
  }

  private BadgeDefinitionAwardEvent getBadgeDefinitionAwardEvent(GenericEventRecord genericEventRecord) {
    List<BaseTag> baseTags = genericEventRecord.getTags();

    AddressTag firstAddressTag = baseTags.stream()
        .filter(AddressTag.class::isInstance)
        .map(AddressTag.class::cast).findFirst().orElseThrow();

    GenericEventRecord firstAddressTagAsEvent = cacheReferencedAddressTagServiceIF.getEvent(firstAddressTag).orElseThrow();

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
