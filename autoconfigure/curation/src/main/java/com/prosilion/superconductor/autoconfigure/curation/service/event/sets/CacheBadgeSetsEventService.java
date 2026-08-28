package com.prosilion.superconductor.autoconfigure.curation.service.event.sets;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeSetsEventService implements CacheBadgeSetsEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;

  public CacheBadgeSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardGenericEventServiceIF;
  }

  @Override
  public Optional<BadgeSetsEvent> materialize(@NonNull EventIF incomingBadgeSetsEvent) {
    log.debug("materialize incomingBadgeSetsEvent:\n  {}", incomingBadgeSetsEvent.createPrettyPrintJson());
    Optional<BadgeSetsEvent> badgeSetsEvent = cacheBadgeDefinitionReputationEventServiceIF
       .getByExpanded(
          incomingBadgeSetsEvent.requireFirstTag(AddressTag.class))
       .map(badgeDefinitionReputationEvent ->
          new BadgeSetsEvent(
             incomingBadgeSetsEvent.asGenericEventRecord(),
             badgeDefinitionReputationEvent,
             incomingBadgeSetsEvent.getTypeSpecificTags(EventTag.class).stream()
                .map(eventTag -> cacheCuratedBadgeAwardGenericEventServiceIF
                   .getEvent(eventTag.eventId(), eventTag.requireRelay()))
                .flatMap(Optional::stream)
                .distinct()
                .toList()));
    return badgeSetsEvent;
  }

  @Override
  public Optional<BadgeSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheReferenceEventTagServiceIF.getEvent(eventId, relay).flatMap(this::materialize);
  }

  @Override
  public Optional<BadgeSetsEvent> getByDirect(@NonNull AddressTag referencedAbstractEventTag) {
    return materializeFirst(cacheKindAddressTagServiceIF.getByDirect(getKind(), referencedAbstractEventTag));
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag referencedAbstractEventTag) {
    return materializeFirst(cacheKindAddressTagServiceIF.getByDirect(getKind(), pubKeyTag, referencedAbstractEventTag));
  }

  @Override
  public List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return materializeList(cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag)).toList();
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(getKind(), pubKeyTag, eventTag));
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
