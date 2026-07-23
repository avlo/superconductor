package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeAwardGenericEventServiceDecorIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

@Slf4j
// TODO: rxr common elements from CacheCuratedBadgeDefinitionGenericEventService into baseClass
public class CacheCuratedBadgeAwardGenericEventService extends CacheCuratedEventService<CuratedBadgeAwardGenericEvent> implements CacheCuratedBadgeAwardGenericEventServiceDecorIF {
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService;

  public CacheCuratedBadgeAwardGenericEventService(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService) {
    super(cacheServiceIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.cacheBadgeAwardGenericEventService = cacheBadgeAwardGenericEventService;
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> materialize(@NonNull EventIF incomingCurationSetsEvent) {
    CuratedBadgeAwardGenericEvent event = new CuratedBadgeAwardGenericEvent(incomingCurationSetsEvent.asGenericEventRecord());

    super.save(event);
    return Optional.of(event);
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    Optional<CuratedBadgeAwardGenericEvent> event = super.getEvent(eventId, relay);
    return event
       .or(() -> cacheBadgeAwardGenericEventService.getEvent(eventId, relay)
          .map(badgeAwardGenericEvent -> createFromFetched(
             badgeAwardGenericEvent, badgeAwardGenericEvent.getRelay().orElseThrow()))
          .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getByDirect(@NonNull EventTag eventTag) {
    return materializeFirst(
       cacheServiceIF.getEventsByKindAndEventTag(getKind(), eventTag))
       .or(() ->
          cacheBadgeAwardGenericEventService.getEvent(eventTag.eventId(), eventTag.requireRelay())
             .map(badgeAwardGenericEvent -> createFromFetched(
                badgeAwardGenericEvent, badgeAwardGenericEvent.getRelay().orElseThrow()))
             .flatMap(this::materialize));
  }

  @Override
  public List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return materializeList(cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag)).toList();
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(getKind(), pubKeyTag, eventTag));
  }

  @Override
  public List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return materializeList(cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag)).toList();
  }

  @Override
  public Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndAddressTag(getKind(), pubKeyTag, addressTag));
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS;
  }

  @Override
  public CuratedBadgeAwardGenericEvent createFromFetched(
     @NonNull BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent,
     @NonNull Relay relay) {
    return new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardGenericEvent,
       new ReferenceTag(relay.getUrl()),
       new ReferenceTag(badgeAwardGenericEvent.getRelayTag().map(RelayTag::getRelay).map(Relay::getUrl).orElseThrow()),
       new Relay(superconductorRelayUrl));
  }
}
