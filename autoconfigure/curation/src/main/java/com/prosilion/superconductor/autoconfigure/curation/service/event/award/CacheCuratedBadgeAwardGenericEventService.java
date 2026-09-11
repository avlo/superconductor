package com.prosilion.superconductor.autoconfigure.curation.service.event.award;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.AbstractCacheCuratedEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeAwardGenericEventServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

@Slf4j
public class CacheCuratedBadgeAwardGenericEventService extends AbstractCacheCuratedEventService<CuratedBadgeAwardGenericEvent, BadgeAwardCanonicalEvent> implements CacheCuratedBadgeAwardGenericEventServiceIF {
  private final CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF;
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;

  public CacheCuratedBadgeAwardGenericEventService(
     @NonNull Identity instanceIdentity,
     @NonNull String relayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF,
     CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF) {
    super(instanceIdentity, relayUrl, cacheServiceIF);
    this.cacheBadgeAwardGenericEventServiceIF = cacheBadgeAwardGenericEventServiceIF;
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  protected CuratedBadgeAwardGenericEvent createFrom(@NonNull GenericEventRecord eventRecord) {
    return new CuratedBadgeAwardGenericEvent(eventRecord);
  }

  @Override
  public List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return findByPubKeyTag(pubKeyTag);
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return findFirstByPubKeyTagAndEventTag(pubKeyTag, eventTag);
  }

  @Override
  public List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return findByPubKeyTagAndIdentifierTag(pubKeyTag, identifierTag);
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return findFirstByPubKeyTagAndAddressTag(pubKeyTag, addressTag);
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getByDirect(@NonNull EventTag eventTag) {
    log.debug("inside CacheCuratedBadgeAwardGenericEventService getByDirect(eventTag) ...");
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
     @NonNull BadgeAwardCanonicalEvent badgeAwardGenericEvent,
     @NonNull Relay relay) {
    log.debug("inside createFromFetched(BadgeAwardCanonicalEvent, Relay) ...");

    log.debug("calling cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(badgeAwardGenericEvent.getBadgeDefinitionEvent().asAddressableEventAddressTag()) ...");
    CuratedBadgeDefinitionGenericEvent curatedBadgeDefinitionGenericEvent = cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(
          badgeAwardGenericEvent.getBadgeDefinitionEvent().asAddressableEventAddressTag())
       .orElseThrow(() -> new NostrException(("unable to find nor construct CuratedBadgeDefinitionGenericEvent")));

    log.debug("sanity check CuratedBadgeDefinitionGenericEvent was created && persisted...");
    if (cacheCuratedBadgeDefinitionGenericEventServiceIF.getEvent(
       curatedBadgeDefinitionGenericEvent.getId(), curatedBadgeDefinitionGenericEvent.requireFirstTag(RelayTag.class).getRelay()).isEmpty()) {
      throw new NostrException(
         String.format("cacheCuratedBadgeDefinitionGenericEventServiceIF.getEvent() failed for eventId: [%s]", curatedBadgeDefinitionGenericEvent.getEventId()));
    }

    log.debug("CuratedBadgeDefinitionGenericEvent retrieved, creating new CuratedBadgeAwardGenericEvent() ...");
    CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent = new CuratedBadgeAwardGenericEvent(
       super.getInstanceIdentity(),
       badgeAwardGenericEvent,
       curatedBadgeDefinitionGenericEvent,
       new ReferenceTag(badgeAwardGenericEvent.getRelayTag().map(RelayTag::getRelay).map(Relay::getUrl).orElseThrow(() -> new NostrException(("revisit what/if exception here thrown")))),
       super.getRelay());
    log.debug("returning curatedBadgeAwardGenericEvent:\n{}", curatedBadgeAwardGenericEvent.createPrettyPrintJson());
    return curatedBadgeAwardGenericEvent;
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_AWARD_EVENT;
  }
}
