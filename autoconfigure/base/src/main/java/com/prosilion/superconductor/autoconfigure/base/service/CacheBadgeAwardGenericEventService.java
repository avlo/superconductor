package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeAwardGenericEventService implements CacheBadgeAwardGenericEventServiceIF {
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;

  public CacheBadgeAwardGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getEvent(@NonNull EventTag eventTag) {
    return getEvent(eventTag.getIdEvent(), eventTag.getRecommendedRelayUrl());
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getEvent(@NonNull String eventId, @NonNull String url) {
    Optional<GenericEventRecord> unpopulatedBadgeAwardGenericVoteEvent = cacheDereferenceEventTagServiceIF.getEvent(new EventTag(eventId, url));
    if (unpopulatedBadgeAwardGenericVoteEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeAwardGenericVoteEvent.get()));
  }

  @Override
  public BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> materialize(@NonNull GenericEventRecord incomingBadgeAwardGenericEvent) {
    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent = getBadgeDefinitionGenericEvent(incomingBadgeAwardGenericEvent);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent = new BadgeAwardGenericEvent<>(
        incomingBadgeAwardGenericEvent.asGenericEventRecord(), aTag -> badgeDefinitionGenericEvent);

    return badgeAwardGenericEvent;
  }

  private BadgeDefinitionGenericEvent getBadgeDefinitionGenericEvent(GenericEventRecord incomingBadgeAwardGenericEvent) {
    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, incomingBadgeAwardGenericEvent).stream().findFirst().orElseThrow(() ->
        new NostrException(
            String.format("%s incoming BadgeAwardGenericEvent %s did not contain an AddressTag", getClass().getSimpleName(), incomingBadgeAwardGenericEvent)));

    Optional<BadgeDefinitionGenericEvent> badgeDefinitionGenericEvent = cacheBadgeDefinitionGenericEventServiceIF.getEvent(addressTag);
    return badgeDefinitionGenericEvent.orElseThrow();
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
