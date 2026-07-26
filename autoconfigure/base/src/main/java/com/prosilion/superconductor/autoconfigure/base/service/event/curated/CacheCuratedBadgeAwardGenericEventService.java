package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeAwardEventServiceIF;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public class CacheCuratedBadgeAwardGenericEventService extends AbstractCacheCuratedEventService<CuratedBadgeAwardGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> implements CacheCuratedBadgeAwardEventServiceIF {
  private final CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF;

  public CacheCuratedBadgeAwardGenericEventService(
     @NonNull Identity instanceIdentity,
     @NonNull String relayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF) {
    super(instanceIdentity, relayUrl, cacheServiceIF);
    this.cacheBadgeAwardGenericEventServiceIF = cacheBadgeAwardGenericEventServiceIF;
  }

  @Override
  protected CuratedBadgeAwardGenericEvent createFrom(@NonNull GenericEventRecord eventRecord) {
    return new CuratedBadgeAwardGenericEvent(eventRecord);
  }

  @Override
  public List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return findByPubKey(pubKeyTag);
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return findFirstByPubKeyAndEvent(pubKeyTag, eventTag);
  }

  @Override
  public List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return findByPubKeyAndIdentifier(pubKeyTag, identifierTag);
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return findFirstByPubKeyAndAddress(pubKeyTag, addressTag);
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getByDirect(@NonNull EventTag eventTag) {
    return findOrCurate(
       () -> findFirstByEventTag(eventTag),
       () -> cacheBadgeAwardGenericEventServiceIF.getEvent(
          eventTag.eventId(), eventTag.requireRelay()),
       badgeAward -> badgeAward.getRelay().orElseThrow());
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getByDirect(@NonNull AddressTag addressTag) {
    return findOrCurate(
       () -> findFirstByAddressTag(addressTag),
       () -> cacheBadgeAwardGenericEventServiceIF.getByDirect(addressTag),
       badgeAward -> badgeAward.getRelay().orElseThrow());
  }

  @Override
  protected CuratedBadgeAwardGenericEvent createFromFetched(
     @NonNull BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent,
     @NonNull Relay relay) {
    return new CuratedBadgeAwardGenericEvent(
       instanceIdentity,
       badgeAwardGenericEvent,
       new ReferenceTag(relay.getUrl()),
       new ReferenceTag(badgeAwardGenericEvent.getRelayTag().map(RelayTag::getRelay).map(Relay::getUrl).orElseThrow()),
       relay);
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_AWARD_EVENT;
  }
}
