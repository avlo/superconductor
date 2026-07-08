package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CacheBadgeDefinitionAbstractEventService<T extends BadgeDefinitionGenericEvent> {
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  private final CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;

  public CacheBadgeDefinitionAbstractEventService(
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF) {
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheReferenceAddressTagServiceIF = cacheReferenceAddressTagServiceIF;
  }

  public abstract Optional<T> materialize(@NonNull EventIF eventIF);


  public Optional<T> getBy(@NonNull AddressTag addressTag) {
    if (!addressTag.getKind().equals(Kind.BADGE_DEFINITION_EVENT))
      throw new NostrException(
         String.format("invalid addressTag.getKind(): [%s] for DefinitionAbstractEvent.  must be kind type [%s]", addressTag.getKind(), Kind.BADGE_DEFINITION_EVENT));

    Optional<GenericEventRecord> badgeDefinitionAbstractEventGEROptional = cacheReferenceAddressTagServiceIF.getBy(addressTag);
    if (badgeDefinitionAbstractEventGEROptional.isEmpty())
      return Optional.empty();

    GenericEventRecord existingBadgeDefinitionReputationEventGER = badgeDefinitionAbstractEventGEROptional.get();
    log.debug("existingBadgeDefinitionReputationEventGER:\n  {}", existingBadgeDefinitionReputationEventGER.createPrettyPrintJson());

    Relay relay = existingBadgeDefinitionReputationEventGER.getRelayTag().map(RelayTag::getRelay).orElseThrow();

    log.debug("calling getEvent(existingBadgeDefinitionReputationEventGER.getId(), relay with eventId:\n  [{}],\n  relayUrl: [{}]",
       existingBadgeDefinitionReputationEventGER.getId(), relay);
    Optional<T> event = getEvent(existingBadgeDefinitionReputationEventGER.getId(), relay);

    if (event.isEmpty()) {
      log.debug("badgeDefinitionReputationEvent.getId()) [%s] not found, return Optional.empty()");
      return Optional.empty();
    }

    log.debug("... returning found badgeDefinitionReputationEvent:\n {}", event.get());
    log.debug("... badgeDefinitionReputationEvent prettyPrintJson:\n {}", event.get().createPrettyPrintJson());

    return event;
  }

  public Optional<T> getBy(@NonNull AddressTag addressTag, @NonNull PubKeyTag pubKeyTag) {
    Optional<T> byAddressTag = getBy(addressTag);
    Optional<T> filterByIdentifierTag = byAddressTag.filter(event -> event.requireFirstTag(PubKeyTag.class).equals(pubKeyTag));
    return filterByIdentifierTag;
  }

  public Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    log.debug("inside getEvent(eventId, relay):\n  [{}],\n  [{}]", eventId, relay);

    Optional<GenericEventRecord> unpopulatedBadgeDefinitionAbstractEvent =
       cacheReferenceEventTagServiceIF.getEvent(eventId, relay);
    log.debug("return unpopulatedBadgeDefinitionAbstractEvent:\n{}",
       unpopulatedBadgeDefinitionAbstractEvent.map(GenericEventRecord::createPrettyPrintJson).orElse("EMPTY OPTIONAL"));

    return unpopulatedBadgeDefinitionAbstractEvent.flatMap(this::materialize);
  }

  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
