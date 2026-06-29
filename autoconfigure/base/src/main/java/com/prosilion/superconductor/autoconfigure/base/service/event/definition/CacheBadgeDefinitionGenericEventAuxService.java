package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventAuxServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeDefinitionGenericEventAuxService implements CacheBadgeDefinitionGenericEventAuxServiceIF {
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;
  private final CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;

  public CacheBadgeDefinitionGenericEventAuxService(
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    this.cacheReferenceAddressTagServiceIF = cacheReferenceAddressTagServiceIF;
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  public Optional<BadgeDefinitionGenericEventAux> materialize(@NonNull EventIF eventIF, Relay relay) {
    log.debug("... materialize(SetsPairedEventTagIF)...\n{}", eventIF);

    Optional<BadgeDefinitionGenericEvent> badgeDefnGenericEventOpt = cacheBadgeDefinitionGenericEventServiceIF.materialize(eventIF);

    Optional<BadgeDefinitionGenericEventAux> badgeDefinitionGenericEventAux = badgeDefnGenericEventOpt.map(badgeDefinitionGenericEvent ->
       new BadgeDefinitionGenericEventAux(
          badgeDefinitionGenericEvent,
          badgeDefinitionGenericEvent.getRelay().orElse(relay)));

    return badgeDefinitionGenericEventAux;
  }

  @Override
  public Optional<BadgeDefinitionGenericEventAux> getBy(AddressTag addressTag, Relay relay) {
    Optional<GenericEventRecord> optGER = cacheReferenceAddressTagServiceIF.getBy(addressTag);

    Optional<BadgeDefinitionGenericEventAux> badgeDefinitionGenericEventAux =
       optGER.flatMap(event -> materialize(event, event.getRelayTag().map(RelayTag::getRelay).orElse(relay)));

    return badgeDefinitionGenericEventAux;
  }

//  public Optional<BadgeDefinitionGenericEventAux> getBy(@NonNull AddressTag addressTag) {
//    log.debug("... inside getBy(addressTag), value: addressTag:  [{}]\npotentially used if event doesn't include RelayTag", addressTag.toStringPrettyPrint());
//
//    if (!addressTag.getKind().equals(Kind.BADGE_DEFINITION_EVENT))
//      throw new NostrException(
//         String.format("invalid addressTag.getKind(): [%s] for DefinitionAbstractEvent.  must be kind type [%s]", addressTag.getKind(), Kind.BADGE_DEFINITION_EVENT));
//
//    Optional<GenericEventRecord> badgeDefinitionAbstractEventGEROptional = cacheReferenceAddressTagServiceIF.getBy(addressTag);
//    if (badgeDefinitionAbstractEventGEROptional.isEmpty())
//      return Optional.empty();
//
//    GenericEventRecord existingBadgeDefinitionReputationEventGER = badgeDefinitionAbstractEventGEROptional.get();
//    log.debug("existingBadgeDefinitionReputationEventGER:\n  {}", existingBadgeDefinitionReputationEventGER.createPrettyPrintJson());
//
//    Optional<String> optEventRelayTagUrl = existingBadgeDefinitionReputationEventGER.getRelayTag()
//       .map(RelayTag::getRelay).map(Relay::getUrl);
//    log.debug("relay url sourced from [ {} ]", optEventRelayTagUrl.isPresent() ? "event" : "addressTag");
//
//    String relayTagUrl = optEventRelayTagUrl.orElse(addressTag.requireRelay().getUrl());
//    log.debug("calling getEvent(existingBadgeDefinitionReputationEventGER.getId(), relayTagUrl with eventId:\n  [{}],\n  relayUrl: [{}]",
//       existingBadgeDefinitionReputationEventGER.getId(), relayTagUrl);
//    Optional<BadgeDefinitionGenericEventAux> event = getEvent(existingBadgeDefinitionReputationEventGER.getId(), relayTagUrl);
//
//    if (event.isEmpty()) {
//      log.debug("badgeDefinitionReputationEvent.getId()) [%s] not found, return Optional.empty()");
//      return Optional.empty();
//    }
//
//    log.debug("... returning found badgeDefinitionReputationEvent:\n {}", event.get());
//
//    return event;
//  }

  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
