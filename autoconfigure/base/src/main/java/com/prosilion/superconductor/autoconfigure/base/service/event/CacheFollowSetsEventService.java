package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEventAux;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventAuxServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheFollowSetsEventService implements CacheFollowSetsEventServiceIF {
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  private final CacheBadgeAwardGenericEventAuxServiceIF<BadgeDefinitionGenericEventAux, BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> cacheBadgeGenericAwardEventServiceIF;
  private final CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;

  public CacheFollowSetsEventService(
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeAwardGenericEventAuxServiceIF<BadgeDefinitionGenericEventAux, BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> cacheBadgeAwardGenericEventAuxServiceIF,
     @NonNull CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF) {
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheBadgeGenericAwardEventServiceIF = cacheBadgeAwardGenericEventAuxServiceIF;
    this.cacheBadgeAwardReputationEventServiceIF = cacheBadgeAwardReputationEventServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
  }

  //  TODO: duplicate in @CacheFormulaEventService, consolidate
  @Override
  public Optional<FollowSetsEvent> getEvent(@NonNull String eventId, @NonNull String url) {
    log.debug("inside getEvent(eventId, url)");
    log.debug("  eventId:  [{}]", eventId);
    log.debug("  relayUrl: [{}]", url);
    Optional<GenericEventRecord> unpopulatedFollowSetsEvent = cacheReferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedFollowSetsEvent.isEmpty()) {
      log.debug("call to cacheReferenceEventTagServiceIF.getEvent(eventId, url) returned EMPTY unpopulatedFormulaEventGER");
      return Optional.empty();
    }

    return materialize(unpopulatedFollowSetsEvent.get());
  }

  @Override
  public Optional<FollowSetsEvent> materialize(@NonNull EventIF incomingFollowSetsEvent) {
    log.debug("materialize(EventIF incomingFollowSetsEvent):\n  {}", incomingFollowSetsEvent.createPrettyPrintJson());

    List<EventTag> voteEventsAsEventTags = incomingFollowSetsEvent.asGenericEventRecord().getTypeSpecificTags(EventTag.class);
//    TODO: revisit throw -vs- return Optional
    if (voteEventsAsEventTags.isEmpty())
      throw new NostrException(
         String.format("FollowSetsEvent [%s] requires at least one EventTag", incomingFollowSetsEvent));

    log.debug("...materializing voteEventEventTagsAsGenericEventRecords using getEventTagEvent()...");
    List<BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> materializedVotesFrommEventTagEvents = voteEventsAsEventTags.stream()
       .map(eventTag -> cacheBadgeGenericAwardEventServiceIF.getEvent(
          eventTag.getIdEvent(),
          eventTag.requireRecommendedRelayUrl()))
       .flatMap(Optional::stream).toList();
    log.debug("...successfully returned materializedVotesFrommEventTagEvents:\n  {}",
       materializedVotesFrommEventTagEvents.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n")));

    Function<EventTag, BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> eventTagToVoteEventFxn = eventTag ->
       materializedVotesFrommEventTagEvents.stream()
          .filter(event -> event.getId().equals(
             eventTag.getIdEvent()))
          .findFirst().orElseThrow(() ->
             new NostrException(
                String.format("FollowSetsEvent materializedVotesFrommEventTagEvents:\n  [%s]\ndid not contain a match for EventTag:\n  [%s]",
                   Util.prettyPrintGenericEventRecords(materializedVotesFrommEventTagEvents.stream()
                      .map(BadgeAwardGenericEventAux::getGenericEventRecord)
                      .toList()),
                   eventTag)));

    log.debug("...getting getTypeSpecificTags(AddressTag)... ");
    AddressTag badgeDefinitionReputationEventAsAddrssTag = incomingFollowSetsEvent.getTypeSpecificTags(AddressTag.class).getFirst();
    log.debug("...incomingFollowSetsEvent.getTypeSpecificTags(AddressTag):\n  {}", badgeDefinitionReputationEventAsAddrssTag.toStringPrettyPrint());

    log.debug("...calling cacheBadgeDefinitionReputationEventServiceIF.getBy(AddressTag)... ");
    BadgeDefinitionReputationEvent existingDefinitionReputationEvent = cacheBadgeDefinitionReputationEventServiceIF
       .getBy(badgeDefinitionReputationEventAsAddrssTag).orElseThrow(() ->
          new NostrException(
             String.format("failed cacheBadgeDefinitionReputationEventServiceIF.getBy(AddressTag) for:\n  [%s]",
                badgeDefinitionReputationEventAsAddrssTag.toStringPrettyPrint())));

    log.debug("... creating FollowSetsEvent with incomingFollowSetsEventGER:\n{}\nmaterializedVotesFrommEventTagEvents:\n  {}\nexistingDefinitionReputationEvent:\n  {}",
       incomingFollowSetsEvent.createPrettyPrintJson(),
       materializedVotesFrommEventTagEvents.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n")),
       existingDefinitionReputationEvent.createPrettyPrintJson());

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
       incomingFollowSetsEvent.asGenericEventRecord(),
       eventTagToVoteEventFxn, addressTag -> existingDefinitionReputationEvent);

    log.debug("...returning materialized FollowSetsEvent:\n{}", followSetsEvent.createPrettyPrintJson());
    return Optional.of(followSetsEvent);
  }

  @Override
  public Optional<BadgeAwardReputationEvent> getBadgeAwardReputationEvent(@NonNull FollowSetsEvent followSetsEvent) {
    log.debug("... calling getBadgeAwardReputationEvent(FollowSetsEventfollowSetsEvent) with followSetsEvent:\n{}", followSetsEvent.createPrettyPrintJson());

    Optional<BadgeAwardReputationEvent> badgeAwardReputationEvent = cacheKindAddressTagServiceIF.getBy(
          Kind.BADGE_AWARD_EVENT,
          new PubKeyTag(followSetsEvent.getAwardRecipientPulicKey()),
          followSetsEvent.getAddressTag())
       .stream()
       .map(event ->
          cacheBadgeAwardReputationEventServiceIF.getEvent(
             event.getId(),
             event.getRelayTag().map(RelayTag::getRelay).map(Relay::getUrl).orElseThrow()))
       .flatMap(Optional::stream)
       .findFirst();

    log.debug("... returning badgeAwardReputationEvent:\n{}", badgeAwardReputationEvent.map(EventIF::createPrettyPrintJson)
       .orElse("returning EMPTY badgeAwardReputationEvent"));

    return badgeAwardReputationEvent;
  }

  @Override
  public Optional<BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> getBy(@NonNull EventTag eventTag) {
    log.debug("getEventTagEvent(@NonNull String eventId, @NonNull String url)");
    Optional<GenericEventRecord> unpopulatedFollowSetsEventTagEvent =
       cacheReferenceEventTagServiceIF.getEvent(eventTag.getIdEvent(), eventTag.requireRecommendedRelayUrl());

    Optional<BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> badgeAwardGenericEvent = unpopulatedFollowSetsEventTagEvent.map(GenericEventRecord::getId).flatMap(id ->
       cacheBadgeGenericAwardEventServiceIF.getEvent(
          id, unpopulatedFollowSetsEventTagEvent.get().getRelayTag().map(RelayTag::getRelay).map(Relay::getUrl).orElseThrow()));

    return badgeAwardGenericEvent;
  }

  @Override
  public Optional<FollowSetsEvent> getBy(@NonNull AddressTag addressTag) {
    return cacheKindAddressTagServiceIF
       .getBy(Kind.FOLLOW_SETS, addressTag)
       .stream()
       .map(this::materialize).flatMap(Optional::stream).findFirst();
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
