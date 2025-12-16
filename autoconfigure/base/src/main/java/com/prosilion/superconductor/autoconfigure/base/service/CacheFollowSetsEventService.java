package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericVoteEvent;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.CacheBadgeAwardGenericVoteEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFollowSetsEventService implements CacheFollowSetsEventServiceIF {
  public static final String NON_EXISTENT_EVENT_ID_S = "FollowSetsEvent [%s] contains AddressTag [%s] referencing non-existent BadgeDefinitionReputationEvent";
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheBadgeAwardGenericVoteEventServiceIF cacheBadgeGenericAwardEventServiceIF;

  public CacheFollowSetsEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheBadgeAwardGenericVoteEventServiceIF cacheBadgeAwardGenericVoteEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheBadgeGenericAwardEventServiceIF = cacheBadgeAwardGenericVoteEventServiceIF;
  }

  @Override
  public void save(@NonNull FollowSetsEvent incomingFollowSetsEvent) {
    List<EventTag> incomingFollowSetsEventTags = incomingFollowSetsEvent.getContainedAddressableEvents();

//    TODO: follow sets event should not exist without at least single event tag, but doesn't necessarily break any logic.  needs follow up
//    if (incomingFollowSetsEventTags.isEmpty())
//      throw new NostrException(
//          String.format("FollowSetsEvent [%s] requires a single AddressTag but had none", incomingFollowSetsEvent.serialize()));

    incomingFollowSetsEventTags
        .forEach(eventTag ->
            cacheDereferenceEventTagServiceIF.getEvent(eventTag).orElseThrow(() ->
                new NostrException(
                    String.format(
                        String.join("", NON_EXISTENT_EVENT_ID_S, "[%s]"),
                        incomingFollowSetsEvent.serialize(),
                        incomingFollowSetsEvent.getId(),
                        eventTag))));

// check for existing follow sets event using pubkey and identifier tag
    Optional<GenericEventRecord> dbOptionalFollowSetsEvent = cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
        incomingFollowSetsEvent.getKind(),
        incomingFollowSetsEvent.getPublicKey(),
        incomingFollowSetsEvent.getIdentifierTag()).stream().findFirst();

    // 	if existing formula event not found:
    if (dbOptionalFollowSetsEvent.isPresent()) {
      log.debug("deleting previous FollowSetsEvent {}...", incomingFollowSetsEvent);
      cacheServiceIF.deleteEvent(dbOptionalFollowSetsEvent.get());
      log.debug("...done");
    }

    log.debug("saving new FollowSetsEvent {}...", incomingFollowSetsEvent);
    cacheServiceIF.save(incomingFollowSetsEvent);
    log.debug("...done");
  }

  @Override
  public Optional<FollowSetsEvent> getEvent(@NonNull String eventId) {
    Optional<GenericEventRecord> unpopulatedFollowSetsEvent = cacheServiceIF.getEventByEventId(eventId);
    if (unpopulatedFollowSetsEvent.isEmpty())
      return Optional.empty();

    List<EventTag> eventTagsOfFollowSetsEvent = Filterable.getTypeSpecificTags(EventTag.class, unpopulatedFollowSetsEvent.get()).stream().toList();

//  TODO: follow sets event should not exist without at least single event tag, but doesn't necessarily break any logic.  needs follow up    
//    if (eventTagsOfFollowSetsEvent.size() != 1)
//      throw new NostrException(
//          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", unpopulatedFollowSetsEvent, eventTagsOfFollowSetsEvent.size()));

    List<GenericEventRecord> badgeAwardEventsAsGenericEventRecords =
        eventTagsOfFollowSetsEvent.stream()
            .map(cacheDereferenceEventTagServiceIF::getEvent)
            .flatMap(Optional::stream)
            .toList();

    List<BadgeAwardGenericVoteEvent> badgeAwardAbstractEvents = badgeAwardEventsAsGenericEventRecords.stream()
        .map(GenericEventRecord::getId)
        .map(cacheBadgeGenericAwardEventServiceIF::getEvent)
        .flatMap(Optional::stream).toList();

    Function<EventTag, BadgeAwardGenericVoteEvent> fxn = eventTag ->
        badgeAwardAbstractEvents.stream().filter(badgeAwardAbstractEvent ->
            badgeAwardAbstractEvent.getId().equals(eventTag.getIdEvent())).findFirst().orElseThrow();

    return Optional.of(cacheDereferenceEventTagServiceIF.createTypedFxnEvent(
        unpopulatedFollowSetsEvent.orElseThrow(),
        FollowSetsEvent.class,
        fxn));
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
