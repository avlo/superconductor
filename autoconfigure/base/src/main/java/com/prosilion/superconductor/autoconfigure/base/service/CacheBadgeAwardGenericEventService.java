package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeAwardGenericEventService implements CacheBadgeAwardGenericEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG_S = "BadgeAwardGenericEvent [%s] contains AddressTag [%s] referencing non-existent BadgeDefinitionReputationEvent";
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;

  public CacheBadgeAwardGenericEventService(
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF) {
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionAwardEvent>> getEvent(@NonNull EventTag eventTag) {
    return getEvent(eventTag.getIdEvent(), eventTag.getRecommendedRelayUrl());
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionAwardEvent>> getEvent(@NonNull String eventId, @NonNull String url) {
    Optional<GenericEventRecord> unpopulatedBadgeAwardGenericVoteEvent = cacheDereferenceEventTagServiceIF.getEvent(new EventTag(eventId, url));
    if (unpopulatedBadgeAwardGenericVoteEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeAwardGenericVoteEvent.get()));
  }
  
  @Override
  public BadgeAwardGenericEvent<BadgeDefinitionAwardEvent> materialize(@NonNull GenericEventRecord incomingBadgeAwardGenericEvent) {
    BadgeDefinitionAwardEvent badgeDefinitionAwardEvent = getBadgeDefinitionAwardEvent(incomingBadgeAwardGenericEvent);

    BadgeAwardGenericEvent<BadgeDefinitionAwardEvent> badgeAwardGenericEvent = new BadgeAwardGenericEvent<>(
        incomingBadgeAwardGenericEvent.asGenericEventRecord(), aTag -> badgeDefinitionAwardEvent);

    return badgeAwardGenericEvent;
  }

  private BadgeDefinitionAwardEvent getBadgeDefinitionAwardEvent(GenericEventRecord incomingBadgeAwardGenericEvent) {
    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, incomingBadgeAwardGenericEvent).stream().findFirst().orElseThrow(() ->
        new NostrException(
            String.format("%s incoming BadgeAwardGenericEvent %s did not contain an AddressTag", getClass().getSimpleName(), incomingBadgeAwardGenericEvent)));

    Optional<GenericEventRecord> event = cacheDereferenceAddressTagServiceIF.getEvent(addressTag);

    GenericEventRecord badgeDefinitionAwardEventGenericEventRecord = event
        .orElseThrow(() ->
            new NostrException(
                String.format(NON_EXISTENT_ADDRESS_TAG_S, incomingBadgeAwardGenericEvent, addressTag)));

    BadgeDefinitionAwardEvent cacheBadgeDefinitionAwardEvent = new BadgeDefinitionAwardEvent(badgeDefinitionAwardEventGenericEventRecord);

    return cacheBadgeDefinitionAwardEvent;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
