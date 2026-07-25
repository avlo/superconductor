package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

@Slf4j
// TODO: rxr common elements from CacheCuratedBadgeAwardGenericEventService into baseClass
public class CacheCuratedBadgeDefinitionGenericEventService extends CacheCuratedEventService<CuratedBadgeDefinitionGenericEvent> implements CacheCuratedBadgeDefinitionGenericEventServiceIF {
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;

  public CacheCuratedBadgeDefinitionGenericEventService(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    super(cacheServiceIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> materialize(@NonNull EventIF incomingCuratedBadgeDefinitionGenericEvent) {
    CuratedBadgeDefinitionGenericEvent event =
       new CuratedBadgeDefinitionGenericEvent(incomingCuratedBadgeDefinitionGenericEvent.asGenericEventRecord());

    super.save(event);
    return Optional.of(event);
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return super.getEvent(eventId, relay)
       .or(() -> cacheBadgeDefinitionGenericEventServiceIF.getEvent(eventId, relay)
          .map(badgeDefinitionGenericEvent -> createFromFetched(
             badgeDefinitionGenericEvent, relay))
          .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull EventTag eventTag) {
    return
       cacheServiceIF.getFirstEventByKindAndEventTag(getKind(), eventTag)
          .flatMap(this::materialize)
          .or(() ->
             cacheBadgeDefinitionGenericEventServiceIF.getEvent(eventTag.eventId(), eventTag.requireRelay())
                .map(badgeDefinitionGenericEvent -> createFromFetched(
                   badgeDefinitionGenericEvent, eventTag.requireRelay()))
                .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull AddressTag addressTag) {
    return
       cacheServiceIF.getFirstEventByKindAndAddressTag(getKind(), addressTag)
          .flatMap(this::materialize)
          .or(() ->
             cacheBadgeDefinitionGenericEventServiceIF.getByExpanded(addressTag)
                .map(badgeDefinitionGenericEvent -> createFromFetched(
                   badgeDefinitionGenericEvent, badgeDefinitionGenericEvent.getRelay().orElseThrow()))
                .flatMap(this::materialize));
  }

  @Override
  public CuratedBadgeDefinitionGenericEvent createFromFetched(
     @NonNull BadgeDefinitionGenericEvent badgeDefinitionGenericEvent,
     @NonNull Relay relay) {
    return new CuratedBadgeDefinitionGenericEvent(
       superconductorInstanceIdentity,
       badgeDefinitionGenericEvent,
       new ReferenceTag(relay.getUrl()),
       new Relay(superconductorRelayUrl));
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_DEFINITION_EVENT;
  }
}
