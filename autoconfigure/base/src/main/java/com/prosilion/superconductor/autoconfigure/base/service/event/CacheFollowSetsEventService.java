package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeSetsEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheFollowSetsEventService implements CacheFollowSetsEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  private final CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;

  public CacheFollowSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheBadgeAwardReputationEventServiceIF = cacheBadgeAwardReputationEventServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
  }

  //  TODO: duplicate in @CacheFormulaEventService, consolidate
  @Override
  public Optional<FollowSetsEvent> materialize(@NonNull EventIF incomingFollowSetsEvent) {
    log.debug("materialize(EventIF incomingFollowSetsEvent):\n  {}", incomingFollowSetsEvent.createPrettyPrintJson());

    List<EventTag> badgeSetsEventsAsEventTags = incomingFollowSetsEvent.asGenericEventRecord().getTypeSpecificTags(EventTag.class);
//    TODO: revisit throw -vs- return Optional
    if (badgeSetsEventsAsEventTags.isEmpty())
      throw new NostrException(String.format("FollowSetsEvent [%s] requires at least one EventTag", incomingFollowSetsEvent));

    log.debug("... calling cacheBadgeSetsEventServiceIF.getEvent(...)");
    List<BadgeSetsEvent> badgeSetsEvents = badgeSetsEventsAsEventTags.stream()
       .map(eventTag -> cacheBadgeSetsEventServiceIF.getBy(
          incomingFollowSetsEvent.requireFirstTag(PubKeyTag.class),
          eventTag))
       .flatMap(Collection::stream).toList();

    log.debug("... returned badgeSetsEvents:\n  [{}]", badgeSetsEvents);

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(incomingFollowSetsEvent.asGenericEventRecord(), badgeSetsEvents);

    log.debug("...returning created materialized FollowSetsEvent:\n{}", followSetsEvent.createPrettyPrintJson());
    return Optional.of(followSetsEvent);
  }

  @Override
  public Optional<FollowSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    log.debug("inside getEvent(eventId, relay)");
    log.debug("  eventId:  [{}]", eventId);
    log.debug("  relayUrl: [{}]", relay);
    log.debug("calling cacheReferenceEventTagServiceIF.getEvent(eventId, relay)...");
    Optional<GenericEventRecord> unpopulatedFollowSetsEvent = cacheReferenceEventTagServiceIF.getEvent(eventId, relay);
    if (unpopulatedFollowSetsEvent.isEmpty()) {
      log.debug("call to cacheReferenceEventTagServiceIF.getEvent(eventId, relay) returned EMPTY unpopulatedFormulaEventGER");
      return Optional.empty();
    }

    log.debug("... cacheReferenceEventTagServiceIF.getEvent() returned:\n  [{}]",
       unpopulatedFollowSetsEvent.map(GenericEventRecord::createPrettyPrintJson));

    return materialize(unpopulatedFollowSetsEvent.get());
  }

  @Override
  public List<BadgeAwardReputationEvent> getBadgeAwardReputationEvents(@NonNull FollowSetsEvent followSetsEvent) {
    log.debug("... calling getBadgeAwardReputationEvent(FollowSetsEventfollowSetsEvent) with followSetsEvent:\n{}",
       followSetsEvent.createPrettyPrintJson());

    List<BadgeAwardReputationEvent> badgeAwardReputationEvents =
       followSetsEvent.getBadgeSetsEventList().stream().flatMap(badgeSetsEvent ->
             cacheKindAddressTagServiceIF.getBy(
                   Kind.BADGE_AWARD_EVENT,
                   new PubKeyTag(followSetsEvent.getAwardRecipientPublicKey()), badgeSetsEvent.getBadgeDefinitionReputationEvent().asAddressableEventAddressTag())
                .stream()
                .map(event ->
                   cacheBadgeAwardReputationEventServiceIF.getEvent(
                      event.getId(),
                      event.getRelayTag().map(RelayTag::getRelay).orElseThrow())))
          .flatMap(Optional::stream).toList();

    log.debug("... returning badgeAwardReputationEvents:\n  [{}]",
       badgeAwardReputationEvents.stream().map(Object::toString).collect(Collectors.joining(",\n  ")));

    return badgeAwardReputationEvents;
  }

  @Override
  public List<FollowSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndAddressTag(getKind(), pubKeyTag, addressTag).stream()
       .map(this::materialize).flatMap(Optional::stream).toList();
  }

//  @Override
//  public Optional<BadgeAwardGenericEventAux> getBy(@NonNull EventTag eventTag) {
//    log.debug("getEventTagEvent(@NonNull String eventId, @NonNull String url)");
//    Optional<GenericEventRecord> unpopulatedFollowSetsEventTagEvent =
//       cacheReferenceEventTagServiceIF.getEvent(eventTag.getIdEvent(), eventTag.requireRecommendedRelayUrl());
//
//    Optional<BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> badgeAwardGenericEvent = unpopulatedFollowSetsEventTagEvent.map(GenericEventRecord::getId).flatMap(id ->
//       cacheBadgeAwardGenericEventAuxServiceIF.getEvent(
//          id, unpopulatedFollowSetsEventTagEvent.get().getRelayTag().map(RelayTag::getRelay).map(Relay::getUrl).orElseThrow()));
//
//    return badgeAwardGenericEvent;
//  }

  @Override
  @Deprecated
  public Optional<FollowSetsEvent> getBy(@NonNull EventTag eventTag) {
    return cacheReferenceEventTagServiceIF.getBy(eventTag).stream()
       .map(this::materialize).flatMap(Optional::stream).findFirst();
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
