package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type.BadgeAwardReputationEventKindTypePlugin;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.service.event.DeleteEventServiceIF;
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
  private final DeleteEventServiceIF deleteEventServiceIF;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;
  private final CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;
  private final BadgeSetsEventKindPlugin badgeSetsEventKindPlugin;
  private final BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin;

  public FollowSetsEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull NotifierService notifierService,
     @NonNull EventPlugin eventPlugin,
     @NonNull DeleteEventServiceIF deleteEventServiceIF,
     @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull BadgeSetsEventKindPlugin badgeSetsEventKindPlugin,
     @NonNull BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin) {
    super(notifierService, eventPlugin);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.deleteEventServiceIF = deleteEventServiceIF;
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
    this.badgeSetsEventKindPlugin = badgeSetsEventKindPlugin;
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
       findRecipientFollowSetsContainingMatchingBadgeDefinitionReputationEvent(
          findAwardRecipientExistingFollowSets(materializedFollowSetsEvent),
          incomingFollowSetsDefnReputationEvents);

    Set<String> voteEventIds = materializedFollowSetsEvent.getBadgeSetsEventList().stream()
       .map(BadgeSetsEvent::getEventTags)
       .flatMap(Collection::stream).map(EventTag::getEventId).collect(Collectors.toSet());

    boolean containsAll = alreadyContainsIncomingVotes(existingDbFollowSets, voteEventIds);
    if (containsAll) {
      return Optional.of(materializedFollowSetsEvent.asGenericEventRecord());
    }

    Set<FollowSetsEvent> followSetsEventToSend = existingDbFollowSets.isEmpty()
       ? Set.of(materializedFollowSetsEvent)
       : rebuildFollowSets(materializedFollowSetsEvent, existingDbFollowSets);

//    log.debug("(10of13V) ... deleting previous existingDbFollowSets::getBadgeSetsEventList ...");
//    existingDbFollowSets.stream()
//       .map(FollowSetsEvent::getBadgeSetsEventList).flatMap(Collection::stream)
//       .forEach(this::deletePrevious);

    log.debug("(10of13V) ... saving new/updated existingDbFollowSets::getBadgeSetsEventList ...");
    followSetsEventToSend.stream()
       .map(FollowSetsEvent::getBadgeSetsEventList).flatMap(Collection::stream)
       .forEach(badgeSetsEvent -> badgeSetsEventKindPlugin.processIncomingEvent(badgeSetsEvent, relay));

    log.debug("(11of13V) ... deleting previous existingDbFollowSets ...");
    existingDbFollowSets.forEach(this::deletePrevious);

    log.debug("(11.5of13V) ... saving new/updated followSetsEventToSend ...");
    followSetsEventToSend.forEach(e -> super.processIncomingEvent(e, relay));

    log.debug("(12of13V) ... calling followSetsEventToSend.foreach(badgeAwardReputationEventKindTypePlugin::processIncomingEvent) ...");
    followSetsEventToSend.forEach(e -> badgeAwardReputationEventKindTypePlugin.processIncomingEvent(e, relay));

    log.debug("(13of13V) ... done.  returning materializedFollowSetsEvent.asGenericEventRecord():\n  {}",
       materializedFollowSetsEvent.createPrettyPrintJson());
    return Optional.of(materializedFollowSetsEvent.asGenericEventRecord());
  }

  private Set<FollowSetsEvent> findAwardRecipientExistingFollowSets(FollowSetsEvent followSetsEvent) {
    PublicKey awardRecipientPublicKey = followSetsEvent.getAwardRecipientPublicKey();
    PubKeyTag pubKeyTag = new PubKeyTag(awardRecipientPublicKey);
    HashSet<FollowSetsEvent> recipientFollowSetsEvents = new HashSet<>(cacheFollowSetsEventServiceIF.getBy(pubKeyTag));
    return recipientFollowSetsEvents;
  }

  private Set<FollowSetsEvent> findRecipientFollowSetsContainingMatchingBadgeDefinitionReputationEvent(Set<FollowSetsEvent> awardRecipientFollowSets, Set<BadgeDefinitionReputationEvent> badgeDefinitions) {
    Set<FollowSetsEvent> recipientFollowSetsContainingMatchingBadgeDefinitionReputationEvent = awardRecipientFollowSets.stream()
       .filter(awardRecipientFollowSetsEvent -> badgeDefinitions.stream()
          
          // anyMatch?
          .anyMatch(badgeDefinition -> awardRecipientFollowSetsEvent.getBadgeSetsEventList().stream()
             
             .map(BadgeSetsEvent::getBadgeDefinitionReputationEvent)
             .map(BadgeDefinitionReputationEvent::asAddressableEventAddressTag)
             
         // all match?
             .allMatch(badgeDefinition.asAddressableEventAddressTag()::equals))) 
       
       .collect(Collectors.toSet());
    return recipientFollowSetsContainingMatchingBadgeDefinitionReputationEvent;
  }

  private boolean alreadyContainsIncomingVotes(Set<FollowSetsEvent> followSetsEvents, Set<String> incomingVoteEventIds) {

    List<String> existingCuratedBadgeAwardEventIds = followSetsEvents.stream()
       .map(FollowSetsEvent::getBadgeSetsEventList)
       .flatMap(Collection::stream)
       .map(BadgeSetsEvent::getCuratedBadgeAwardGenericEventList)
       .flatMap(Collection::stream)
       .map(CuratedBadgeAwardGenericEvent::getId).toList();

    boolean allMatch = existingCuratedBadgeAwardEventIds.containsAll(incomingVoteEventIds);
    boolean followSetsNonEmptyAndAllBadgeAwardEventIdsMatch = !followSetsEvents.isEmpty() && allMatch;
    return followSetsNonEmptyAndAllBadgeAwardEventIdsMatch;
  }

  private FollowSetsEvent rebuildFollowSets(FollowSetsEvent materializedFollowSetsEvent, FollowSetsEvent existingFollowSetsEvent) {
    FollowSetsEvent newFromExisting = existingFollowSetsEvent.createNewFromExisting(
       superconductorInstanceIdentity,
       materializedFollowSetsEvent.getBadgeSetsEventList());
    return newFromExisting;
  }

  private Set<FollowSetsEvent> rebuildFollowSets(FollowSetsEvent materializedFollowSetsEvent, Set<FollowSetsEvent> existingFollowSetsEvents) {
    Set<FollowSetsEvent> collect = existingFollowSetsEvents.stream()
       .map(existingFollowSetsEvent ->
          rebuildFollowSets(materializedFollowSetsEvent, existingFollowSetsEvent))
       .collect(Collectors.toSet());
    return collect;
  }

  private void deletePrevious(EventIF eventIF) {
    deleteEventServiceIF.processIncomingEvent(eventIF, new Relay(superconductorRelayUrl));
  }

  @Override
  public Kind getKind() {
    log.debug("getKind Kind[{}]: {}",
       Kind.FOLLOW_SETS.getValue(),
       Kind.FOLLOW_SETS.getName().toUpperCase());
    return Kind.FOLLOW_SETS;
  }
}
