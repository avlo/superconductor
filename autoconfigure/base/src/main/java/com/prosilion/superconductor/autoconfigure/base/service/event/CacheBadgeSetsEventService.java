package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeSetsEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheCurationSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeSetsEventService implements CacheBadgeSetsEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheCurationSetsEventServiceIF cacheCurationSetsEventServiceIF;

  public CacheBadgeSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheCurationSetsEventServiceIF cacheCurationSetsEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
    this.cacheCurationSetsEventServiceIF = cacheCurationSetsEventServiceIF;
  }

  @Override
  public Optional<BadgeSetsEvent> materialize(@NonNull EventIF incomingBadgeSetsEvent) {
    log.debug("materialize(EventIF incomingBadgeSetsEvent):\n  {}", incomingBadgeSetsEvent.createPrettyPrintJson());
    return
       cacheBadgeDefinitionReputationEventServiceIF
          .getBy(
             incomingBadgeSetsEvent.requireFirstTag(AddressTag.class),
             new PubKeyTag(new PublicKey(
                incomingBadgeSetsEvent.requireFirstTag(IdentifierTag.class).getUuid())))
          .map(badgeDefinitionReputationEvent ->
             new BadgeSetsEvent(
                incomingBadgeSetsEvent.asGenericEventRecord(),
                badgeDefinitionReputationEvent,
                incomingBadgeSetsEvent.getTypeSpecificTags(EventTag.class).stream()
                   .map(eventTag -> cacheCurationSetsEventServiceIF
                      .getEvent(eventTag.eventId(), eventTag.requireRelay()))
                   .flatMap(Optional::stream)
                   .distinct()
                   .toList()));
  }

  @Override
  public Optional<BadgeSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    log.debug("inside getEvent(eventId, relay):\n  event [{}]\n  relay [{}]", eventId, relay);
    log.debug("calling cacheDereferenceEventTagService.getEvent(eventId, relay)...");
    return cacheServiceIF.getEventByEventId(eventId).flatMap(this::materialize);
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull AddressTag referencedAbstractEventTag) {
    log.debug("inside getBy(@NonNull AddressTag [{}]", referencedAbstractEventTag);
    log.debug("calling cacheReferenceAddressTagService.getBy(referencedAbstractEventTag)...");
    return materialize(cacheKindAddressTagServiceIF.getBy(getKind(), referencedAbstractEventTag).getFirst());
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag referencedAbstractEventTag) {
    log.debug("inside getBy(@NonNull AddressTag [{}]", referencedAbstractEventTag);
    log.debug("calling cacheReferenceAddressTagService.getBy(referencedAbstractEventTag)...");
    return materialize(cacheKindAddressTagServiceIF.getBy(getKind(), pubKeyTag, referencedAbstractEventTag).getFirst());
  }

  @Override
  public List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return materialize(cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag)).toList();
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return materialize(cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(getKind(), pubKeyTag, eventTag).getFirst());
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return materialize(cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag).getFirst());
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
  
  private Stream<BadgeSetsEvent> materialize(List<GenericEventRecord> genericEventRecords) {
    return genericEventRecords.stream()
       .mapMulti((genericEventRecord, downstreamBadgeSetsEventConsumer) ->
          materialize(genericEventRecord).ifPresent(downstreamBadgeSetsEventConsumer));
  }
}
