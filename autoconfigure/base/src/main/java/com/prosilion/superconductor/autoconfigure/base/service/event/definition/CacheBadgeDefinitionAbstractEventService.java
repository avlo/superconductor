package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public abstract class CacheBadgeDefinitionAbstractEventService<T extends BadgeDefinitionGenericEvent> {
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;

  public CacheBadgeDefinitionAbstractEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
  }

  public abstract T materialize(@NonNull EventIF eventIF);

  public Optional<T> getExistingDefinitionEvent(@NonNull GenericEventRecord genericEventRecord) {
    AddressTag addressTag = genericEventRecord.requireFirstTag(AddressTag.class);

    if (!addressTag.getKind().equals(Kind.BADGE_DEFINITION_EVENT))
      throw new NostrException(
          String.format("invalid addressTag.getKind(): [%s] for DefinitionAbstractEvent.  must be kind type [%s]", addressTag.getKind(), Kind.BADGE_DEFINITION_EVENT));

    Optional<GenericEventRecord> badgeDefinitionAbstractEventGEROptional = cacheDereferenceAddressTagServiceIF.getEvent(addressTag);
    if (badgeDefinitionAbstractEventGEROptional.isEmpty())
      throw new NostrException(
          String.format("cacheDereferenceAddressTagServiceIF.getEvent(addressTag) using addressTag:\n  %s\nnot found", Util.prettyPrintAddressTags(addressTag)));

    GenericEventRecord existingBadgeDefinitionReputationEventGER = badgeDefinitionAbstractEventGEROptional.get();
    log.debug("existingBadgeDefinitionReputationEventGER:\n  {}", existingBadgeDefinitionReputationEventGER);

    String relayTagUrl = existingBadgeDefinitionReputationEventGER.getRelayTagUrl();

    log.debug("calling getEvent(existingBadgeDefinitionReputationEventGER.getId(), relayTagUrl with eventId: [{}], relayUrl: [{}]",
        existingBadgeDefinitionReputationEventGER.getId(), relayTagUrl);
    Optional<T> event = getEvent(existingBadgeDefinitionReputationEventGER.getId(), relayTagUrl);

    if (event.isEmpty()) {
      log.debug("badgeDefinitionReputationEvent.getId()) [%s] not found, throwing exception");
      throw new NostrException(
          String.format("getEvent(badgeDefinitionReputationEvent.getId()) [%s] not found", existingBadgeDefinitionReputationEventGER.getId()));
    }

    log.debug("... returning found badgeDefinitionReputationEvent:\n {}", event.get());
    log.debug("... badgeDefinitionReputationEvent prettyPrintJson:\n {}", event.get().createPrettyPrintJson());

    return event;
  }

  public Optional<T> getEvent(@NonNull String eventId, @NonNull String url) {
    log.debug("inside getEvent(eventId, url): [{}], [{}]", eventId, url);

    Optional<GenericEventRecord> unpopulatedBadgeDefinitionAbstractEvent =
        cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    log.debug("return unpopulatedBadgeDefinitionAbstractEvent:\n{}",
        unpopulatedBadgeDefinitionAbstractEvent.map(GenericEventRecord::createPrettyPrintJson).orElse("EMPTY OPTIONAL"));

    return unpopulatedBadgeDefinitionAbstractEvent.map(this::materialize);

  }

  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
