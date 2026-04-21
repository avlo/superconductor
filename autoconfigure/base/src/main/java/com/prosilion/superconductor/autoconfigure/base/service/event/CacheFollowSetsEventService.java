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
import java.util.stream.Collectors;
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
    log.debug("materialize(EventIF incomingFollowSetsEvent):\n  {}", incomingFollowSetsEvent.createPrettyPrintJson());

    List<EventTag> voteEventsAsEventTags = Filterable.getTypeSpecificTags(EventTag.class, incomingFollowSetsEvent).stream().toList();

    if (voteEventsAsEventTags.isEmpty())
      throw new NostrException(
          String.format("FollowSetsEvent [%s] requires at least one EventTag", incomingFollowSetsEvent));

    log.debug("...calling cacheDereferenceEventTagServiceIF.getEvents(voteEventsAsEventTags)...");
    List<GenericEventRecord> voteEventEventTagsAsGenericEventRecords =
        cacheDereferenceEventTagServiceIF.getEvents(
            voteEventsAsEventTags).stream().toList();
    log.debug("...successfully returned voteEventEventTagsAsGenericEventRecords...");

    log.debug("...materializing voteEventEventTagsAsGenericEventRecords using getEventTagEvent()...");
    List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> materializedEventTagEvents =
        voteEventEventTagsAsGenericEventRecords.stream()
            .map(genericEventRecord ->
                getEventTagEvent(
                    genericEventRecord.getId(),
                    Filterable.getTypeSpecificTagsStream(RelayTag.class, genericEventRecord)
                        .findFirst().orElseThrow(() ->
                            new NostrException(
                                String.format("FollowSetsEvent did not contain a RelayTag\n  [%s]", genericEventRecord.createPrettyPrintJson())))
                        .getRelay().getUrl()))
            .flatMap(Optional::stream).toList();
    log.debug("...successfully returned materializedEventTagEvents...");

    Function<EventTag, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> eventTagToVoteEventFxn = eventTag ->
        materializedEventTagEvents.stream()
            .filter(event -> event.getId().equals(
                eventTag.getIdEvent()))
            .findFirst().orElseThrow(() ->
                new NostrException(
                    String.format("FollowSetsEvent materializedEventTagEvents:\n  [%s]\ndid not contain a match for EventTag:\n  [%s]",
                        materializedEventTagEvents.stream()
                            .map(EventIF::createPrettyPrintJson)
                            .collect(Collectors.joining(",\n  ")),
                        eventTag)));

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
        incomingFollowSetsEvent.asGenericEventRecord(), eventTagToVoteEventFxn);

    return followSetsEvent;
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getEventTagEvent(@NonNull String eventId, @NonNull String url) {
    log.debug("getEventTagEvent(@NonNull String eventId, @NonNull String url)");
    Optional<GenericEventRecord> unpopulatedFollowSetsEventTagEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);

    if (unpopulatedFollowSetsEventTagEvent.isEmpty()) {
      log.debug("unpopulatedFollowSetsEventTagEvent GenericEventRecord not found, throw exception");
      throw new NostrException(
          String.format("FollowSetsEvent EventTag's GenericEventRecord eventId: [%s], url: [%s] not found", eventId, url));
    }

    log.debug("unpopulatedFollowSetsEventTagEvent GenericEventRecord found, call cacheBadgeGenericAwardEventServiceIF.getEvent(unpopulatedFollowSetsEventTagEvent.getId, url) to populate it");
    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> event = cacheBadgeGenericAwardEventServiceIF.getEvent(unpopulatedFollowSetsEventTagEvent.get().getId(), url);
    log.debug("returning populated BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>:\n  {}", event.orElseThrow().createPrettyPrintJson());
    return event;
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
}
