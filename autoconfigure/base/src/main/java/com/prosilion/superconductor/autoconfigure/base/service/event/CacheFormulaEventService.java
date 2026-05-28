package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheDereferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
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
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheFormulaEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagService cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String eventId, @NonNull String url) {
    log.debug("insdice getEvent(@NonNull String eventId, @NonNull String url)");
    log.debug("  eventId:  [{}]", eventId);
    log.debug("  relayUrl: [{}]", url);
    Optional<GenericEventRecord> unpopulatedFormulaEventGER = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedFormulaEventGER.isEmpty()) {
      log.debug("call to cacheDereferenceEventTagServiceIF.getEvent(eventId, url) returned EMPTY unpopulatedFormulaEventGER", url);
      return Optional.empty();
    }

    log.debug("call to cacheDereferenceEventTagServiceIF.getEvent(eventId, url) returned unpopulatedFormulaEventGER:\n  {}", unpopulatedFormulaEventGER.get().createPrettyPrintJson());

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
  public Optional<FormulaEvent> getAddressTagAsFormulaEvent(@NonNull AddressTag addressTag) {
    log.debug("getAddressTagAsFormulaEvent(AddressTag addressTag):{}", Util.prettyPrintAddressTags(addressTag));

    Optional<GenericEventRecord> formulaEventGER = cacheKindAddressTagServiceIF.getEventByKindAndAddressTag(
        Kind.ARBITRARY_CUSTOM_APP_DATA, addressTag);

    if (formulaEventGER.isEmpty()) {
      log.debug("cacheKindAddressTagServiceIF.getEventByKindAndAddressTag(ARBITRARY_CUSTOM_APP_DATA, addressTag) returned EMPTY formulaEventGER");
      return Optional.empty();
    }

    log.debug("cacheKindAddressTagServiceIF.getEventByKindAndAddressTag(ARBITRARY_CUSTOM_APP_DATA, addressTag) returned formulaEventGER:\n  {}", formulaEventGER.get().createPrettyPrintJson());
    log.debug("formulaEventGER eventId: [{}]", formulaEventGER.get().getId());

    String formulaEventRelayUrl = formulaEventGER.get().getRelayTagUrl();
    log.debug("formulaEventGER relayUrl: [{}]", formulaEventRelayUrl);

    Optional<FormulaEvent> formulaEvent = getEvent(formulaEventGER.get().getId(), formulaEventRelayUrl);
    log.debug("returning formulaEvent:\n  {}", formulaEvent.get().createPrettyPrintJson());

    return formulaEvent;
  }

  private BadgeDefinitionGenericEvent getBadgeDefinitionGenericEvent(@NonNull GenericEventRecord unpopulatedFormulaEventGER) {
    log.debug("inside getBadgeDefinitionGenericEvent(GenericEventRecord unpopulatedFormulaEventGER");

    log.debug("calling unpopulatedFormulaEventGER.requireFirstTag(AddressTag.class)");
    AddressTag firstAddressTag = unpopulatedFormulaEventGER.requireFirstTag(AddressTag.class);
    log.debug("returned firstAddressTag:\n  {}", Util.prettyPrintAddressTags(firstAddressTag));

    log.debug("calling cacheDereferenceAddressTagServiceIF.getEvent(firstAddressTag)");
    GenericEventRecord firstAddressTagAsEventGER = cacheDereferenceAddressTagServiceIF.getEvent(firstAddressTag).orElseThrow(() ->
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
