package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
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

  public CacheFollowSetsEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventServiceIF) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheBadgeGenericAwardEventServiceIF = cacheBadgeAwardGenericEventServiceIF;
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
    List<EventTag> voteEventsAsEventTags = Filterable.getTypeSpecificTags(EventTag.class, incomingFollowSetsEvent).stream().toList();

    if (voteEventsAsEventTags.isEmpty())
      throw new NostrException(
          String.format("FollowSetsEvent [%s] requires at least one EventTag", incomingFollowSetsEvent));

    List<GenericEventRecord> voteEventGenericEventRecords = voteEventsAsEventTags.stream()
        .map(eventTag ->
            cacheDereferenceEventTagServiceIF.getEvent(eventTag).orElseThrow(() ->
                new NostrException(
                    String.format(NON_EXISTENT_EVENT_ID_S, incomingFollowSetsEvent, eventTag))))
        .toList();

    Function<EventTag, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> eventTagToVoteEventFxn = eventTag ->
        voteEventGenericEventRecords.stream()
            .filter(badgeAwardGenericEvent ->
                badgeAwardGenericEvent.getId().equals(eventTag.getIdEvent()))
            .map(genericEventRecord ->
                getEventTagEvent(
                    genericEventRecord.getId(),
                    Filterable.getTypeSpecificTagsStream(RelayTag.class, genericEventRecord).findFirst().orElseThrow().getRelay().getUrl()))
            .flatMap(Optional::stream)
            .findFirst().orElseThrow();

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
        incomingFollowSetsEvent.asGenericEventRecord(), eventTagToVoteEventFxn);

    return followSetsEvent;
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getEventTagEvent(@NonNull String eventId, @NonNull String url) {
//  TODO: follow sets event should not exist without at least single event tag, but doesn't necessarily break any logic.  needs follow up    
//    if (eventTagsOfFollowSetsEvent.size() != 1)
//      throw new NostrException(
//          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", unpopulatedFollowSetsEvent, eventTagsOfFollowSetsEvent.size()));    
    Optional<GenericEventRecord> unpopulatedFollowSetsEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedFollowSetsEvent.isEmpty())
      return Optional.empty();

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeGenericAwardEvent = getCacheBadgeGenericAwardEvent(unpopulatedFollowSetsEvent.get(), url);

    return cacheBadgeGenericAwardEvent;
  }

//  @Override
//  public List<FollowSetsEvent> getEventsByPubkeyTag(@NonNull PublicKey badgeAwardRecipientPublicKey) {
//    List<GenericEventRecord> eventsByKindAndPubKeyTag = cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), badgeAwardRecipientPublicKey);
//
//    List<String> eventIds = eventsByKindAndPubKeyTag.stream()
//        .map(GenericEventRecord::id).toList();
//
//    List<FollowSetsEvent> list = eventIds.stream()
//        .map(eventId -> this.getEventTagEvent(eventId, superconductorRelayUrl)).flatMap(Optional::stream).toList();
//
//    return list;
//  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }

  private Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getCacheBadgeGenericAwardEvent(GenericEventRecord genericEventRecord, String url) {
    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> event = cacheBadgeGenericAwardEventServiceIF.getEvent(genericEventRecord.getId(), url);
    return event;
  }
}
