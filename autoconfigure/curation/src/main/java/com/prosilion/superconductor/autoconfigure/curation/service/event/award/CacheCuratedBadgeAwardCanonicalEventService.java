package com.prosilion.superconductor.autoconfigure.curation.service.event.award;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardCanonicalEvent;
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
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardCanonicalEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeAwardCanonicalEventServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheCuratedBadgeAwardCanonicalEventService extends
   AbstractCacheCuratedEventService<
         CuratedBadgeAwardCanonicalEvent,
         BadgeAwardCanonicalEvent,
         EventTag> implements
   CacheCuratedBadgeAwardCanonicalEventServiceIF {
  private final CacheBadgeAwardCanonicalEventServiceIF cacheBadgeAwardCanonicalEventServiceIF;
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;

  public CacheCuratedBadgeAwardCanonicalEventService(
     @NonNull Identity instanceIdentity,
     @NonNull String relayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeAwardCanonicalEventServiceIF cacheBadgeAwardCanonicalEventServiceIF,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF) {
    super(instanceIdentity, relayUrl, cacheServiceIF);
    this.cacheBadgeAwardCanonicalEventServiceIF = cacheBadgeAwardCanonicalEventServiceIF;
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
  }

  @Override
  protected CuratedBadgeAwardCanonicalEvent createFrom(@NonNull GenericEventRecord eventRecord) {
    return new CuratedBadgeAwardCanonicalEvent(eventRecord);
  }

  @Override
  public Optional<CuratedBadgeAwardCanonicalEvent> getByExpanded(@NonNull EventTag referencedAbstractEventTag) {
    log.debug("inside getByExpanded(@NonNull EventTag referencedAbstractEventTag)");
    Optional<GenericEventRecord> curatedBadgeAwardCanonicalEventGER = cacheReferenceEventTagServiceIF.getByExpanded(referencedAbstractEventTag, getKind());
    log.debug("returned curatedBadgeAwardCanonicalEventGER:\n {}",
       curatedBadgeAwardCanonicalEventGER.map(GenericEventRecord::createPrettyPrintJson).orElse("[ EMPTY OPTIONAL ]"));

    Optional<CuratedBadgeAwardCanonicalEvent> curatedBadgeAwardCanonicalEventOpt = curatedBadgeAwardCanonicalEventGER.flatMap(this::materialize);
    log.debug("materialized curatedBadgeAwardCanonicalEventOpt:\n {}",
       curatedBadgeAwardCanonicalEventOpt.map(CuratedBadgeAwardCanonicalEvent::createPrettyPrintJson).orElse("[ EMPTY OPTIONAL ]"));
    return curatedBadgeAwardCanonicalEventOpt;
  }

  @Override
  public List<CuratedBadgeAwardCanonicalEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return findByPubKeyTag(pubKeyTag);
  }

  @Override
  public Optional<CuratedBadgeAwardCanonicalEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return findFirstByPubKeyTagAndEventTag(pubKeyTag, eventTag);
  }

  @Override
  public List<CuratedBadgeAwardCanonicalEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return findByPubKeyTagAndIdentifierTag(pubKeyTag, identifierTag);
  }

  @Override
  public Optional<CuratedBadgeAwardCanonicalEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return findFirstByPubKeyTagAndAddressTag(pubKeyTag, addressTag);
  }

  @Override
  public Optional<CuratedBadgeAwardCanonicalEvent> getByDirect(@NonNull EventTag eventTag) {
    log.debug("inside CacheCuratedBadgeAwardCanonicalEventService getByDirect(eventTag) ...");
    return findOrCurate(
       () -> findFirstByEventTag(eventTag),
       () -> cacheBadgeAwardCanonicalEventServiceIF.getEvent(
          eventTag.eventId(), eventTag.requireRelay()),
       badgeAward -> badgeAward.getRelay().orElseThrow());
  }

  @Override
  public Optional<CuratedBadgeAwardCanonicalEvent> getByDirect(@NonNull AddressTag addressTag) {
    return findOrCurate(
       () -> findFirstByAddressTag(addressTag),
       () -> cacheBadgeAwardCanonicalEventServiceIF.getByDirect(addressTag),
       badgeAward -> badgeAward.getRelay().orElseThrow());
  }

  @Override
  protected CuratedBadgeAwardCanonicalEvent createFromFetched(
     @NonNull BadgeAwardCanonicalEvent badgeAwardCanonicalEvent,
     @NonNull Relay relay) {
    log.debug("inside createFromFetched(BadgeAwardCanonicalEvent, Relay) ...");

    log.debug("calling cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(badgeAwardCanonicalEvent.getBadgeDefinitionEvent().asAddressableEventAddressTag()) ...");
    CuratedBadgeDefinitionGenericEvent curatedBadgeDefinitionGenericEvent = cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(
          badgeAwardCanonicalEvent.getBadgeDefinitionEvent().asAddressableEventAddressTag())
       .orElseThrow(() -> new NostrException(("unable to find nor construct CuratedBadgeDefinitionGenericEvent")));

    log.debug("sanity check CuratedBadgeDefinitionGenericEvent was created && persisted...");
    if (cacheCuratedBadgeDefinitionGenericEventServiceIF.getEvent(
       curatedBadgeDefinitionGenericEvent.getId(),
       curatedBadgeDefinitionGenericEvent.requireFirstTag(RelayTag.class).getRelay()).isEmpty()) {
      throw new NostrException(
         String.format("cacheCuratedBadgeDefinitionGenericEventServiceIF.getEvent() failed for eventId: [%s]", curatedBadgeDefinitionGenericEvent.getEventId()));
    }

    log.debug("createFromFetched(...) retrieved CuratedBadgeDefinitionGenericEvent, creating new CuratedBadgeAwardCanonicalEvent() ...");
    CuratedBadgeAwardCanonicalEvent curatedBadgeAwardCanonicalEvent = new CuratedBadgeAwardCanonicalEvent(
       super.getInstanceIdentity(),
       badgeAwardCanonicalEvent,
       curatedBadgeDefinitionGenericEvent,
       new ReferenceTag(badgeAwardCanonicalEvent.getRelayTag().map(RelayTag::getRelay).map(Relay::getUrl).orElseThrow(() -> new NostrException(("revisit what/if exception here thrown")))),
       super.getRelay());
    log.debug("  {}", curatedBadgeAwardCanonicalEvent.createPrettyPrintJson());
    return curatedBadgeAwardCanonicalEvent;
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_AWARD_EVENT;
  }
}
