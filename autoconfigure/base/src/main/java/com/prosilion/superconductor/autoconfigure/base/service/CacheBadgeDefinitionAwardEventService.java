package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionAwardEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionAwardEventService implements CacheBadgeDefinitionAwardEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG = "BadgeAwardGenericEvent [%s] is missing required AddressTag";
  public static final String NON_EXISTENT_DEFINITION_EVENT = "BadgeAwardGenericEvent [%s] contains AddressTag [%s] referencing non-existent BadgeDefinitionAwardEvent [%s]";
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;

  public CacheBadgeDefinitionAwardEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
  }

  @Override
  public Optional<BadgeDefinitionAwardEvent> getEvent(@NonNull EventTag eventTag) {
    return getEvent(eventTag.getIdEvent(), eventTag.getRecommendedRelayUrl());
  }

  @Override
  public Optional<BadgeDefinitionAwardEvent> getEvent(@NonNull String eventId, @NonNull String url) {
    Optional<GenericEventRecord> unpopulatedBadgeDefinitionAwardEvent = cacheDereferenceEventTagServiceIF.getEvent(new EventTag(eventId));
    if (unpopulatedBadgeDefinitionAwardEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeDefinitionAwardEvent.get()));
  }

  @Override
  public BadgeDefinitionAwardEvent materialize(@NonNull GenericEventRecord incomingBadgeDefinitionAwardEvent) {
    BadgeDefinitionAwardEvent badgeDefinitionAwardEvent = getBadgeDefinitionAwardEvent(incomingBadgeDefinitionAwardEvent);
    return badgeDefinitionAwardEvent;
  }

  private BadgeDefinitionAwardEvent getBadgeDefinitionAwardEvent(@NonNull GenericEventRecord genericEventRecord) {
    AddressTag firstAddressTag = Filterable.getTypeSpecificTagsStream(AddressTag.class, genericEventRecord)
        .findFirst().orElseThrow(() ->
            new NostrException(
                String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord)));

    GenericEventRecord firstAddressTagAsEvent = cacheDereferenceAddressTagServiceIF.getEvent(firstAddressTag).orElseThrow(() ->
        new NostrException(
            String.format(NON_EXISTENT_DEFINITION_EVENT, genericEventRecord)));

    BadgeDefinitionAwardEvent addressTagNowBadgeDefinitionAwardEvent = new BadgeDefinitionAwardEvent(firstAddressTagAsEvent);

    return addressTagNowBadgeDefinitionAwardEvent;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
