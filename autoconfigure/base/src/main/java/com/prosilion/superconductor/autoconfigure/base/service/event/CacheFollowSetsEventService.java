package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFollowSetsEventService implements CacheFollowSetsEventServiceIF {
  public static final String NON_EXISTENT_EVENT_ID_S = "FollowSetsEvent [%s] contains EventTag [%s] referencing non-existent BadgeAwardGeneric(Upvote/Downvote)Event";
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeGenericAwardEventServiceIF;
  private final CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;

  public CacheFollowSetsEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventServiceIF,
      @NonNull CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventServiceIF,
      @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
      @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheBadgeGenericAwardEventServiceIF = cacheBadgeAwardGenericEventServiceIF;
    this.cacheBadgeAwardReputationEventServiceIF = cacheBadgeAwardReputationEventServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
  }

  @Override
  public Optional<FollowSetsEvent> getEvent(@NonNull String eventId, @NonNull String url) {
    Optional<GenericEventRecord> unpopulatedFollowSetsEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedFollowSetsEvent.isEmpty())
      return Optional.empty();

    FollowSetsEvent materialize = materialize(unpopulatedFollowSetsEvent.get());
    return Optional.of(materialize);
  }

  @Override
  public FollowSetsEvent materialize(@NonNull EventIF incomingFollowSetsEvent) {
    log.debug("materialize(EventIF incomingFollowSetsEvent):\n  {}", incomingFollowSetsEvent.createPrettyPrintJson());

    List<EventTag> voteEventsAsEventTags = incomingFollowSetsEvent.asGenericEventRecord().getTypeSpecificTags(EventTag.class);
    if (voteEventsAsEventTags.isEmpty())
      throw new NostrException(
          String.format("FollowSetsEvent [%s] requires at least one EventTag", incomingFollowSetsEvent));

    log.debug("...materializing voteEventEventTagsAsGenericEventRecords using getEventTagEvent()...");
    List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> materializedEventTagEvents = voteEventsAsEventTags.stream()
        .map(eventTag -> cacheBadgeGenericAwardEventServiceIF.getEvent(
            eventTag.getIdEvent(),
            eventTag.getRecommendedRelayUrl()))
        .flatMap(Optional::stream).toList();
    log.debug("...successfully returned materializedEventTagEvents...");

    Function<EventTag, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> eventTagToVoteEventFxn = eventTag ->
        materializedEventTagEvents.stream()
            .filter(event -> event.getId().equals(
                eventTag.getIdEvent()))
            .findFirst().orElseThrow(() ->
                new NostrException(
                    String.format("FollowSetsEvent materializedEventTagEvents:\n  [%s]\ndid not contain a match for EventTag:\n  [%s]",
                        Util.prettyPrintGenericEventRecords(materializedEventTagEvents.stream()
                            .map(BadgeAwardGenericEvent::getGenericEventRecord)
                            .toList()),
                        eventTag)));

    BadgeDefinitionReputationEvent existingReputationDefinitionEvent = cacheBadgeDefinitionReputationEventServiceIF.getExistingDefinitionEvent(
        incomingFollowSetsEvent.asGenericEventRecord()).orElseThrow();
    log.debug("... existingReputationDefinitionEvent:\n{}", existingReputationDefinitionEvent.createPrettyPrintJson());

    log.debug("... creating FollowSetsEvent with incomingFollowSetsEventGER:\n{}\nmaterializedEventTagEvents:\n  {}\nexistingReputationDefinitionEvent:\n  {}",
        incomingFollowSetsEvent.createPrettyPrintJson(),
        materializedEventTagEvents.stream().map(EventIF::createPrettyPrintJson),
        existingReputationDefinitionEvent.createPrettyPrintJson());

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
        incomingFollowSetsEvent.asGenericEventRecord(),
        eventTagToVoteEventFxn, addressTag -> existingReputationDefinitionEvent);

    log.debug("...returning materialized FollowSetsEvent:\n{}", followSetsEvent.createPrettyPrintJson());
    return followSetsEvent;
  }

  @Override
  public Optional<BadgeAwardReputationEvent> getBadgeAwardReputationEvent(@NonNull FollowSetsEvent followSetsEvent) {
    AddressTag addressableAddressTag = followSetsEvent.getAddressTag();
    log.debug("... calling getBadgeAwardReputationEvent(FollowSetsEventfollowSetsEvent) with followSetsEvent:\n{}", followSetsEvent.createPrettyPrintJson());
    Optional<BadgeAwardReputationEvent> badgeAwardReputationEvent = cacheKindAddressTagServiceIF.getEventByKindAndPubKeyTagAndAddressTag(
            Kind.BADGE_AWARD_EVENT,
            new PubKeyTag(followSetsEvent.getAwardRecipientPulicKey()),
            addressableAddressTag)
        .flatMap(event ->
            cacheBadgeAwardReputationEventServiceIF.getEvent(
                    event.getId(),
                    event.getRelayTagUrl())
                .stream().findFirst());

    log.debug("... returning badgeAwardReputationEvent:\n{}", badgeAwardReputationEvent.map(EventIF::createPrettyPrintJson)
        .orElse("returning EMPTY badgeAwardReputationEvent"));

    return badgeAwardReputationEvent;
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getEventTagEvent(@NonNull EventTag eventTag) {
    log.debug("getEventTagEvent(@NonNull String eventId, @NonNull String url)");
    Optional<GenericEventRecord> unpopulatedFollowSetsEventTagEvent =
        cacheDereferenceEventTagServiceIF.getEvent(eventTag.getIdEvent(), eventTag.getRecommendedRelayUrl());

    if (unpopulatedFollowSetsEventTagEvent.isEmpty()) {
      log.debug("unpopulatedFollowSetsEventTagEvent GenericEventRecord not found, throw exception");
      throw new NostrException(
          String.format("FollowSetsEvent EventTag's GenericEventRecord eventId: [%s], url: [%s] not found",
              eventTag.getIdEvent(), eventTag.getRecommendedRelayUrl()));
    }

    log.debug("unpopulatedFollowSetsEventTagEvent GenericEventRecord found, call cacheBadgeGenericAwardEventServiceIF.getEvent(unpopulatedFollowSetsEventTagEvent.getId, url) to populate it");
    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> event = cacheBadgeGenericAwardEventServiceIF.getEvent(unpopulatedFollowSetsEventTagEvent.get().getId(), unpopulatedFollowSetsEventTagEvent.get().getRelayTagUrl());
    log.debug("returning populated BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>:\n  {}", event.orElseThrow().createPrettyPrintJson());
    return event;
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
