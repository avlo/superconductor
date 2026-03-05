package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeAwardGenericEventService implements CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> {
  public static final String NOT_FOUND = "BadgeDefinitionGenericEvent with eventId [%s] and url [%s] not found";
  public static final String MISSING_ADDRESS_TAG = "%s incoming BadgeAwardGenericEvent %s did not contain an AddressTag";
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;

  public CacheBadgeAwardGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getEvent(@NonNull String eventId, @NonNull String url) {
    Optional<GenericEventRecord> unpopulatedBadgeAwardGenericEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedBadgeAwardGenericEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeAwardGenericEvent.get()));
  }

  @Override
  public BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> materialize(@NonNull EventIF incomingBadgeAwardGenericEvent) {
    BadgeDefinitionGenericEvent dbBadgeDefinitionGenericEvent = getBadgeDefinitionGenericEvent(incomingBadgeAwardGenericEvent.asGenericEventRecord());

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
        new BadgeAwardGenericEvent<>(
            incomingBadgeAwardGenericEvent.asGenericEventRecord(),
            addressTag -> dbBadgeDefinitionGenericEvent);

    return badgeAwardGenericEvent;
  }

  private BadgeDefinitionGenericEvent getBadgeDefinitionGenericEvent(GenericEventRecord incomingBadgeAwardGenericEvent) {
    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, incomingBadgeAwardGenericEvent).stream().findFirst().orElseThrow(() ->
        new NostrException(
            String.format(MISSING_ADDRESS_TAG, getClass().getSimpleName(), incomingBadgeAwardGenericEvent)));

    Optional<BadgeDefinitionGenericEvent> badgeDefinitionGenericEvent =
        cacheBadgeDefinitionGenericEventServiceIF.getAddressTagEvent(addressTag);
    return badgeDefinitionGenericEvent.orElseThrow();
  }

  @Override
  public Optional<BadgeDefinitionGenericEvent> getEventTagEvent(@NonNull String eventId, @NonNull String url) {
    Optional<GenericEventRecord> unpopulatedBadgeDefinitionGenericEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedBadgeDefinitionGenericEvent.isEmpty())
      throw new NostrException(String.format(NOT_FOUND, eventId, url));

    return Optional.of(getBadgeDefinitionGenericEvent(unpopulatedBadgeDefinitionGenericEvent.get()));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
