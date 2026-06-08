package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;

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

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String eventId, @NonNull String url) {
    log.debug("insdice getEvent(@NonNull String eventId, @NonNull String url)");
    log.debug("  eventId:  [{}]", eventId);
    log.debug("  relayUrl: [{}]", url);
    Optional<GenericEventRecord> unpopulatedFormulaEventGER = cacheReferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedFormulaEventGER.isEmpty()) {
      log.debug("call to cacheReferenceEventTagServiceIF.getEvent(eventId, url) returned EMPTY unpopulatedFormulaEventGER", url);
      return Optional.empty();
    }

    log.debug("call to cacheReferenceEventTagServiceIF.getEvent(eventId, url) returned unpopulatedFormulaEventGER:\n  {}", unpopulatedFormulaEventGER.get().createPrettyPrintJson());

    log.debug("calling materialize(unpopulatedFormulaEvent.get()) ...", url);
    return Optional.of(materialize(unpopulatedFormulaEventGER.get()));
  }

  @Override
  public FormulaEvent materialize(@NonNull EventIF incomingFormulaEvent) {
    log.debug("inside materialize(EventIF incomingFormulaEvent):\n  {}", incomingFormulaEvent.createPrettyPrintJson());

    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent = getBadgeDefinitionGenericEvent(incomingFormulaEvent.asGenericEventRecord());
    log.debug("getBadgeDefinitionGenericEvent(incomingFormulaEvent):\n  {}", badgeDefinitionGenericEvent.createPrettyPrintJson());

    FormulaEvent formulaEvent = new FormulaEvent(
        incomingFormulaEvent.asGenericEventRecord(),
        addressTag -> badgeDefinitionGenericEvent);

    log.debug("return reCreated formulaEvent:\n  {}", formulaEvent.createPrettyPrintJson());
    return formulaEvent;
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    log.debug("getBy(pubKeyTag, AddressTag):\n{}", addressTag.toStringPrettyPrint());
    Optional<GenericEventRecord> formulaEventAsOptGER = cacheKindAddressTagServiceIF
        .getBy(Kind.ARBITRARY_CUSTOM_APP_DATA, pubKeyTag, addressTag).stream().findFirst();
    return getFormulaEventById(formulaEventAsOptGER);
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag, @NonNull Relay relay) {
    AddressTag addressTag = new AddressTag(Kind.ARBITRARY_CUSTOM_APP_DATA, publicKey, identifierTag, relay);
    log.debug("calling cacheReferenceAddressTagServiceIF.getBy(addressTag) with:\n{}", addressTag.toStringPrettyPrint());

    Optional<GenericEventRecord> formulaEventGERs = cacheReferenceAddressTagServiceIF.getBy(addressTag);
    return getFormulaEvents(formulaEventGERs.stream().toList());
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull AddressTag addressTag) {
    log.debug("getBy(AddressTag):\n{}", addressTag.toStringPrettyPrint());

    List<GenericEventRecord> formulaEventGERs = cacheKindAddressTagServiceIF.getBy(Kind.ARBITRARY_CUSTOM_APP_DATA, addressTag);
    return getFormulaEvents(formulaEventGERs);
  }

  @NonNull
  private Optional<FormulaEvent> getFormulaEvents(List<GenericEventRecord> formulaEventGERs) {
    log.debug("cacheKindAddressTagServiceIF.getBy(Kind.ARBITRARY_CUSTOM_APP_DATA, addressTag) returned:");
    log.debug("formulaEventGERs size:  [{}]", formulaEventGERs.size());
    log.debug("formulaEventGERs contents:\n  {}", formulaEventGERs.stream().map(GenericEventRecord::createPrettyPrintJson));

    Optional<GenericEventRecord> formulaEventAsOptGER = formulaEventGERs.stream().findFirst();
    log.debug("first formulaEventGER:\n  {}", formulaEventAsOptGER.map(GenericEventRecord::createPrettyPrintJson));

    Optional<FormulaEvent> formulaEvent = getFormulaEventById(formulaEventAsOptGER);
    log.debug("get first formulaEventGER:\n{}", formulaEvent.map(EventIF::createPrettyPrintJson).orElse("OPTIONAL EMPTY"));
    return formulaEvent;
  }

  @NonNull
  private Optional<FormulaEvent> getFormulaEventById(Optional<GenericEventRecord> formulaEventOptGER) {
    if (formulaEventOptGER.isEmpty())
      return Optional.empty();

    log.debug("getFormulaEvent(formulaEventOptGER):\n  {}", formulaEventOptGER.get().createPrettyPrintJson());
    log.debug("formulaEventOptGER eventId: [{}]", formulaEventOptGER.get().getId());

    String formulaEventRelayUrl = formulaEventOptGER.get().getRelayTagUrl();
    log.debug("formulaEventOptGER relayUrl: [{}]", formulaEventRelayUrl);

    Optional<FormulaEvent> formulaEvent = getEvent(formulaEventOptGER.get().getId(), formulaEventRelayUrl);
    log.debug("returning formulaEvent:\n  {}", formulaEvent.get().createPrettyPrintJson());

    return formulaEvent;
  }

  private BadgeDefinitionGenericEvent getBadgeDefinitionGenericEvent(@NonNull GenericEventRecord unpopulatedFormulaEventGER) {
    log.debug("inside getBadgeDefinitionGenericEvent(GenericEventRecord unpopulatedFormulaEventGER");

    log.debug("calling unpopulatedFormulaEventGER.requireFirstTag(AddressTag.class)");
    AddressTag firstAddressTag = unpopulatedFormulaEventGER.requireFirstTag(AddressTag.class);
    log.debug("returned firstAddressTag:\n  {}", Util.prettyPrintAddressTags(firstAddressTag));

    log.debug("calling cacheReferenceAddressTagServiceIF.getEvent(firstAddressTag)");
    GenericEventRecord firstAddressTagAsEventGER = cacheReferenceAddressTagServiceIF.getBy(firstAddressTag).orElseThrow(() ->
        new NostrException(
            String.format(NON_EXISTENT_BADGE_DEFINITION_AWARD_EVENT_S, unpopulatedFormulaEventGER)));
    log.debug("returned unpopulatedFormulaEventGER's firstAddressTagAsEventGER (is a BadgeDefinition[Upvote]Event):\n  {}", firstAddressTagAsEventGER.createPrettyPrintJson());

    BadgeDefinitionGenericEvent addressTagNowBadgeDefinitionGenericEvent = new BadgeDefinitionGenericEvent(firstAddressTagAsEventGER);
    log.debug("returneding addressTagNowBadgeDefinitionGenericEvent:\n  {}", addressTagNowBadgeDefinitionGenericEvent.createPrettyPrintJson());

    return addressTagNowBadgeDefinitionGenericEvent;
  }

  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
