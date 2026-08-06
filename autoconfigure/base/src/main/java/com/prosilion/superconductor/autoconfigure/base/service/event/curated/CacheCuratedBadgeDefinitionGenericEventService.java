package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import java.util.Optional;
import lombok.NonNull;

public class CacheCuratedBadgeDefinitionGenericEventService extends AbstractCacheCuratedEventService<CuratedBadgeDefinitionGenericEvent, BadgeDefinitionGenericEvent> implements CacheCuratedBadgeDefinitionGenericEventServiceIF {
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;

  public CacheCuratedBadgeDefinitionGenericEventService(
     @NonNull Identity instanceIdentity,
     @NonNull String relayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    super(instanceIdentity, relayUrl, cacheServiceIF);
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  protected CuratedBadgeDefinitionGenericEvent createFrom(@NonNull GenericEventRecord eventRecord) {
    return new CuratedBadgeDefinitionGenericEvent(eventRecord);
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull EventTag eventTag) {
    return findOrCurate(
       () -> findFirstByEventTag(eventTag),
       () -> cacheBadgeDefinitionGenericEventServiceIF.getEvent(
          eventTag.eventId(), eventTag.requireRelay()),
       ignored -> eventTag.requireRelay());
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull AddressTag addressTag) {
    return findOrCurate(
       () -> findFirstByAddressTag(addressTag),
       () -> cacheBadgeDefinitionGenericEventServiceIF.getByExpanded(addressTag),
       badgeDefinition -> badgeDefinition.getRelay().orElseThrow());
  }

  @Override
  protected CuratedBadgeDefinitionGenericEvent createFromFetched(
     @NonNull BadgeDefinitionGenericEvent badgeDefinitionGenericEvent,
     @NonNull Relay relay) {
    return new CuratedBadgeDefinitionGenericEvent(
       instanceIdentity,
       badgeDefinitionGenericEvent,
       new ReferenceTag(relay.getUrl()),
       relay);
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_DEFINITION_EVENT;
  }
}
