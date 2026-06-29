package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheFormulaEventService implements CacheFormulaEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG = "FormulaEvent [%s] is missing required AddressTag";
  public static final String NON_EXISTENT_BADGE_DEFINITION_AWARD_EVENT_S = "FormulaEvent [%s] contains AddressTag referencing non-existent BadgeDefinitionGenericEvent";
  public static final String FORMATTED = "formula event found with matching author public key and identifier tag (UUID) but with different formula:\n  (db) [%s]\n    -vs- (incoming formula) [%s]\n";
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  private final CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheFormulaEventService(
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheReferenceAddressTagServiceIF = cacheReferenceAddressTagServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  //  TODO: duplicate in @CacheFollowsEventSeervice, consolidate  
  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    log.debug("inside getEvent(eventId, relay)");
    log.debug("  eventId:  [{}]", eventId);
    log.debug("  relayUrl: [{}]", relay);
    Optional<GenericEventRecord> unpopulatedFormulaEventGER = cacheReferenceEventTagServiceIF.getEvent(eventId, relay);
    if (unpopulatedFormulaEventGER.isEmpty()) {
      log.debug("call to cacheReferenceEventTagServiceIF.getEvent(eventId, relay) returned EMPTY unpopulatedFormulaEventGER");
      return Optional.empty();
    }

    log.debug("call to cacheReferenceEventTagServiceIF.getEvent(eventId, relay) returned unpopulatedFormulaEventGER:\n  {}", unpopulatedFormulaEventGER.get().createPrettyPrintJson());

    log.debug("calling materialize(unpopulatedFormulaEvent.get()) ...", relay);
    return materialize(unpopulatedFormulaEventGER.get());
  }

  @Override
  public Optional<FormulaEvent> materialize(@NonNull EventIF incomingFormulaEvent) {
    log.debug("inside materialize(EventIF incomingFormulaEvent):\n  {}", incomingFormulaEvent.createPrettyPrintJson());
    return cacheReferenceAddressTagServiceIF
       .getBy(incomingFormulaEvent.requireFirstTag(AddressTag.class))
       .map(BadgeDefinitionGenericEvent::new)
       .map(event ->
          new FormulaEvent(
             incomingFormulaEvent.asGenericEventRecord(),
             addressTag -> event));
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag, @NonNull Relay relay) {
    AddressTag addressTag = new AddressTag(Kind.ARBITRARY_CUSTOM_APP_DATA, publicKey, identifierTag, relay);
    log.debug("calling cacheReferenceAddressTagServiceIF.getBy(addressTag) with:\n{}", addressTag.toStringPrettyPrint());

    return cacheReferenceAddressTagServiceIF.getBy(addressTag).flatMap(event -> getFormulaEventById(event));
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull AddressTag addressTag) {
    log.debug("getBy(AddressTag):\n{}", addressTag.toStringPrettyPrint());

    Optional<GenericEventRecord> formulaEventGERs = cacheKindAddressTagServiceIF.getBy(Kind.ARBITRARY_CUSTOM_APP_DATA, addressTag).stream().findFirst();
    log.debug("formulaEventGERs contents:\n  {}", formulaEventGERs.stream().map(GenericEventRecord::createPrettyPrintJson));

    return formulaEventGERs.flatMap(event -> getFormulaEventById(event));
  }

  @NonNull
  private Optional<FormulaEvent> getFormulaEventById(GenericEventRecord formulaEventOptGER) {
    log.debug("getFormulaEvent(formulaEventOptGER):\n  {}", formulaEventOptGER.createPrettyPrintJson());
    log.debug("formulaEventOptGER eventId: [{}]", formulaEventOptGER.getId());

    Relay formulaEventRelayUrl = formulaEventOptGER.getRelayTag().map(RelayTag::getRelay).orElse(null);
    log.debug("formulaEventOptGER relayUrl: [{}]", formulaEventRelayUrl);

    Optional<FormulaEvent> formulaEvent = getEvent(formulaEventOptGER.getId(), formulaEventRelayUrl);
    log.debug("returning formulaEvent:\n  {}", formulaEvent.map(EventIF::createPrettyPrintJson).orElse("FORMULA EVENT: EMPTY OPTIONAL "));

    return formulaEvent;
  }

  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
