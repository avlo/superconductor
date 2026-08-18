package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type.BadgeAwardReputationEventKindTypePlugin;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.event.plugin.kind.type.DeleteEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FollowSetsEventKindPlugin extends PublishingEventKindPlugin { // kind 30_000
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final DeleteEventKindPlugin deleteEventKindPlugin;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;
  private final CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;
  private final BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin;

  public FollowSetsEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull NotifierService notifierService,
     @NonNull EventPlugin eventPlugin,
     @NonNull DeleteEventKindPlugin deleteEventKindPlugin,
     @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF,
     @NonNull CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin) {
    super(notifierService, eventPlugin);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.deleteEventKindPlugin = deleteEventKindPlugin;
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardGenericEventServiceIF;
    this.badgeAwardReputationEventKindTypePlugin = badgeAwardReputationEventKindTypePlugin;
    log.debug("using superconductorRelayUrl: [{}]", superconductorRelayUrl);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFollowSetsEvent, @NonNull Relay relay) {
    FollowSetsEvent materializedFollowSetsEvent = cacheFollowSetsEventServiceIF.materialize(incomingFollowSetsEvent).orElseThrow();
    log.debug("materializedFollowSetsEvent:\n{}", materializedFollowSetsEvent.createPrettyPrintJson());

    Set<BadgeDefinitionReputationEvent> incomingFollowSetsDefnReputationEvents =
       materializedFollowSetsEvent.getBadgeSetsEventList().stream()
          .map(BadgeSetsEvent::getBadgeDefinitionReputationEvent).collect(Collectors.toSet());

    Set<FollowSetsEvent> existingDbFollowSets =
       findMatchingFollowSets(
          findAwardRecipientExistingFollowSets(
             materializedFollowSetsEvent,
             incomingFollowSetsDefnReputationEvents),
          incomingFollowSetsDefnReputationEvents);

    Set<String> voteEventIds = materializedFollowSetsEvent.getBadgeSetsEventList().stream()
       .map(BadgeSetsEvent::getEventTags)
       .flatMap(Collection::stream).map(EventTag::getEventId).collect(Collectors.toSet());

    if (alreadyContainsIncomingVotes(existingDbFollowSets, voteEventIds)) {
      return Optional.of(materializedFollowSetsEvent.asGenericEventRecord());
    }

    Set<FollowSetsEvent> followSetsEventToSend = existingDbFollowSets.isEmpty()
       ? Set.of(materializedFollowSetsEvent)
       : rebuildFollowSets(materializedFollowSetsEvent, existingDbFollowSets);

    log.debug("(10of13V) ... deleting previous existingDbFollowSets via forEach(this::deletePreviousFollowSetsEvent) ...");
    existingDbFollowSets.forEach(this::deletePreviousFollowSetsEvent);

    log.debug("(11of13V) ... saving new/updated existingDbFollowSets via existingDbFollowSets.forEach(super.processIncomingEvent) ...");
    followSetsEventToSend.forEach(e -> super.processIncomingEvent(e, relay));

    log.debug("(12of13V) ... calling followSetsEventToSend.foreach(badgeAwardReputationEventKindTypePlugin::processIncomingEvent) ...");
    followSetsEventToSend.forEach(e -> badgeAwardReputationEventKindTypePlugin.processIncomingEvent(e, relay));

    log.debug("(13of13V) ... done.  returning materializedFollowSetsEvent.asGenericEventRecord():\n  {}",
       materializedFollowSetsEvent.createPrettyPrintJson());
    return Optional.of(materializedFollowSetsEvent.asGenericEventRecord());

  }

  private Set<FollowSetsEvent> findAwardRecipientExistingFollowSets(FollowSetsEvent followSetsEvent, Set<BadgeDefinitionReputationEvent> defnReputationEvents) {
    PublicKey awardRecipientPublicKey = followSetsEvent.getAwardRecipientPublicKey();
    PubKeyTag pubKeyTag = new PubKeyTag(awardRecipientPublicKey);
    List<FollowSetsEvent> by = cacheFollowSetsEventServiceIF.getBy(pubKeyTag);
    HashSet<FollowSetsEvent> followSetsEvents = new HashSet<>(by);
    return followSetsEvents;
  }

  private Set<FollowSetsEvent> findMatchingFollowSets(Set<FollowSetsEvent> awardRecipientFollowSets, Set<BadgeDefinitionReputationEvent> badgeDefinitions) {
    return awardRecipientFollowSets.stream()
       .filter(awardRecipientFollowSetsEvent -> badgeDefinitions.stream()
          .anyMatch(badgeDefinition -> awardRecipientFollowSetsEvent.getBadgeSetsEventList().stream()
             .map(BadgeSetsEvent::getBadgeDefinitionReputationEvent)
             .map(BadgeDefinitionReputationEvent::asAddressableEventAddressTag)
             .anyMatch(badgeDefinition.asAddressableEventAddressTag()::equals)))
       .collect(Collectors.toSet());
  }

  private boolean alreadyContainsIncomingVotes(Set<FollowSetsEvent> followSetsEvents, Set<String> incomingVoteEventIds) {
    return !followSetsEvents.isEmpty() && followSetsEvents.stream()
       .allMatch(followSetsEvent -> followSetsEvent.getBadgeSetsEventList().stream()
          .map(BadgeSetsEvent::getEventTags)
          .flatMap(Collection::stream)
          .map(EventTag::getEventId)
          .anyMatch(incomingVoteEventIds::contains));
  }

  private Set<FollowSetsEvent> rebuildFollowSets(FollowSetsEvent materializedFollowSetsEvent, Set<FollowSetsEvent> existingFollowSetsEvents) {
    return
       materializedFollowSetsEvent.getBadgeSetsEventList().stream()
          .map(BadgeSetsEvent::getEventTags)
          .flatMap(Collection::stream)
          .map(eventTag -> cacheCuratedBadgeAwardGenericEventServiceIF.getByDirect(eventTag).orElseThrow())
          .flatMap(badgeAwardEvent -> existingFollowSetsEvents.stream()
             .map(existingFollowSetsEvent -> existingFollowSetsEvent.createNewFromExisting(
                superconductorInstanceIdentity,
                existingFollowSetsEvent.getBadgeSetsEventList().stream()
                   .map(existingBadgeSetsEvent -> existingBadgeSetsEvent.createNewFromExisting(
                      superconductorInstanceIdentity, badgeAwardEvent))
                   .toList())))
          .collect(Collectors.toSet());
  }

  private void deletePreviousFollowSetsEvent(FollowSetsEvent previousFollowSetsEvent) {
    deleteEventKindPlugin.processIncomingEvent(previousFollowSetsEvent, new Relay(superconductorRelayUrl));
  }

  @Override
  public Kind getKind() {
    log.debug("getKind Kind[{}]: {}",
       Kind.FOLLOW_SETS.getValue(),
       Kind.FOLLOW_SETS.getName().toUpperCase());
    return Kind.FOLLOW_SETS;
  }
}
