package com.prosilion.superconductor.autoconfigure.curation.service.event.definition;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.AbstractCacheCuratedEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    Optional<CuratedBadgeDefinitionGenericEvent> orCurate = findOrCurate(
       () -> getFirstByAddressTag(addressTag),
       () -> getByExpanded(addressTag),
       badgeDefinition -> badgeDefinition.getRelay().or(() -> addressTag.findRelay()).orElseThrow());
    return orCurate;
  }

  private Optional<CuratedBadgeDefinitionGenericEvent> getFirstByAddressTag(@NonNull AddressTag addressTag) {
    log.debug("attempting getFirstByAddressTag(AddressTag):\n {}", addressTag.toStringPrettyPrint());
    Optional<CuratedBadgeDefinitionGenericEvent> firstByAddressTag = findFirstByAddressTag(addressTag);
    log.debug(firstByAddressTag.map(BaseEvent::createPrettyPrintJson).orElse(
       "findFirstByAddressTag(addressTag) returned Optional.empty()"));
    return firstByAddressTag;
  }

  private Optional<BadgeDefinitionGenericEvent> getByExpanded(@NonNull AddressTag addressTag) {
    log.debug("attempting getByExpanded(AddressTag):\n {}", addressTag.toStringPrettyPrint());
    Optional<BadgeDefinitionGenericEvent> byExpanded = cacheBadgeDefinitionGenericEventServiceIF.getByExpanded(addressTag);
    log.debug(byExpanded.map(BaseEvent::createPrettyPrintJson).orElse(
       "cacheBadgeDefinitionGenericEventServiceIF.getByExpanded(addressTag) returned Optional.empty()"));
    return byExpanded;
  }

  @Override
  protected CuratedBadgeDefinitionGenericEvent createFromFetched(
     @NonNull BadgeDefinitionGenericEvent badgeDefinitionGenericEvent,
     @NonNull Relay relay) {
    return new CuratedBadgeDefinitionGenericEvent(
       super.getInstanceIdentity(),
       badgeDefinitionGenericEvent,
       new ReferenceTag(relay.getUrl()),
       super.getRelay());
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_DEFINITION_EVENT;
  }
}
