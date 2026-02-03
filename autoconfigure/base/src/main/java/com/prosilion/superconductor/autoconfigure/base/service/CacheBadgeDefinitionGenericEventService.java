package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionGenericEventService implements CacheBadgeDefinitionGenericEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG = "BadgeAwardGenericEvent [%s] is missing required AddressTag";
  public static final String NON_EXISTENT_DEFINITION_EVENT = "BadgeAwardGenericEvent [%s] contains AddressTag references non-existent BadgeDefinitionGenericEvent relay [%s]";
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;

  public CacheBadgeDefinitionGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
  }

  @Override
  public Optional<BadgeDefinitionGenericEvent> getEvent(@NonNull AddressTag addressTag) {
    Optional<GenericEventRecord> unpopulatedBadgeDefinitionGenericEvent = cacheDereferenceAddressTagServiceIF.getEvent(addressTag);
    if (unpopulatedBadgeDefinitionGenericEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeDefinitionGenericEvent.get()));
  }

  @Override
  public BadgeDefinitionGenericEvent materialize(@NonNull GenericEventRecord genericEventRecord) {
    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent = getBadgeDefinitionGenericEvent(genericEventRecord);
    return badgeDefinitionGenericEvent;
  }

  private BadgeDefinitionGenericEvent getBadgeDefinitionGenericEvent(@NonNull GenericEventRecord genericEventRecord) {
    RelayTag relayTag = Filterable.getTypeSpecificTagsStream(RelayTag.class, genericEventRecord)
        .findFirst().orElseThrow(() ->
            new NostrException(
                String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord)));
    
    GenericEventRecord firstAddressTagAsEvent = cacheDereferenceEventTagServiceIF.getEvent(
        genericEventRecord.getId(),
        relayTag.getRelay().getUrl()).orElseThrow(() ->
        new NostrException(
            String.format(NON_EXISTENT_DEFINITION_EVENT, genericEventRecord, relayTag.getRelay())));

    BadgeDefinitionGenericEvent addressTagNowBadgeDefinitionGenericEvent = new BadgeDefinitionGenericEvent(firstAddressTagAsEvent);

    return addressTagNowBadgeDefinitionGenericEvent;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
