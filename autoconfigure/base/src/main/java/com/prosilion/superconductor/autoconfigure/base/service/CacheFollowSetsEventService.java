package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.CacheBadgeAwardGenericEventServiceIF;
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
  private final CacheBadgeAwardGenericEventServiceIF cacheBadgeGenericAwardEventServiceIF;

  public CacheFollowSetsEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheBadgeGenericAwardEventServiceIF = cacheBadgeAwardGenericEventServiceIF;
  }

  @Override
  public FollowSetsEvent materialize(@NonNull EventIF incomingFollowSetsEvent) {
    List<EventTag> eventTagsOfFollowSetsEvent = Filterable.getTypeSpecificTags(EventTag.class, incomingFollowSetsEvent).stream().toList();

    if (eventTagsOfFollowSetsEvent.isEmpty())
      throw new NostrException(
          String.format("FollowSetsEvent [%s] requires at least one EventTag", incomingFollowSetsEvent));

    List<GenericEventRecord> badgeAwardEventsAsGenericEventRecords =
        eventTagsOfFollowSetsEvent.stream()
            .map(cacheDereferenceEventTagServiceIF::getEvent)
            .flatMap(Optional::stream)
            .toList();

    List<BadgeAwardGenericEvent<BadgeDefinitionAwardEvent>> badgeAwardAbstractEvents = badgeAwardEventsAsGenericEventRecords.stream()
//        .map(GenericEventRecord::getId)
        .map(cacheBadgeGenericAwardEventServiceIF::materialize)
//        .flatMap(Optional::stream)
        .toList();

    Function<EventTag, BadgeAwardGenericEvent<BadgeDefinitionAwardEvent>> fxn = eventTag ->
        getBadgeAwardGenericEventStream(badgeAwardAbstractEvents, eventTag).stream().findFirst().orElseThrow();

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
        incomingFollowSetsEvent.asGenericEventRecord(), fxn);

    return reconstruct(followSetsEvent);
  }

  private FollowSetsEvent reconstruct(@NonNull FollowSetsEvent incomingFollowSetsEvent) {
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

//    log.debug("saving new FollowSetsEvent {}...", incomingFollowSetsEvent);
//    cacheServiceIF.save(incomingFollowSetsEvent);
//    log.debug("...done");
    return incomingFollowSetsEvent;
  }

  @Override
  public Optional<FollowSetsEvent> getEvent(@NonNull String eventId) {
//  TODO: follow sets event should not exist without at least single event tag, but doesn't necessarily break any logic.  needs follow up    
//    if (eventTagsOfFollowSetsEvent.size() != 1)
//      throw new NostrException(
//          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", unpopulatedFollowSetsEvent, eventTagsOfFollowSetsEvent.size()));    
    return cacheServiceIF.getEventByEventId(eventId)
        .map(genericEventRecord ->
            new FollowSetsEvent(genericEventRecord, eventTag ->
                getBadgeAwardGenericEventStream(Filterable.getTypeSpecificTags(EventTag.class, genericEventRecord).stream()
                    .map(cacheDereferenceEventTagServiceIF::getEvent)
                    .flatMap(Optional::stream)
                    .map(GenericEventRecord::getId)
                    .map(cacheBadgeGenericAwardEventServiceIF::getEvent)
                    .flatMap(Optional::stream).toList(), eventTag).stream().findFirst().orElseThrow(() ->
                    new NostrException(
                        String.format("FollowSetsEvent [%s] either missing an EventTag or EventTag does not have mappable BadgeGenericAwardEvent", genericEventRecord)))));
  }

  @Override
  public List<FollowSetsEvent> getEventsByPubkeyTag(@NonNull PublicKey badgeAwardRecipientPublicKey) {
    return cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), badgeAwardRecipientPublicKey).stream()
        .map(GenericEventRecord::id)
        .map(this::getEvent).flatMap(Optional::stream).toList();
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }

  private List<BadgeAwardGenericEvent<BadgeDefinitionAwardEvent>> getBadgeAwardGenericEventStream(
      List<BadgeAwardGenericEvent<BadgeDefinitionAwardEvent>> badgeAwardAbstractEvents,
      EventTag eventTag) {
    List<BadgeAwardGenericEvent<BadgeDefinitionAwardEvent>> badgeAwardGenericEvents = badgeAwardAbstractEvents.stream().filter(badgeAwardGenericEvent ->
    {
      String id = badgeAwardGenericEvent.getId();
      String idEvent = eventTag.getIdEvent();
      boolean equals = id.equals(idEvent);
      return equals;
    }).toList();
    return badgeAwardGenericEvents;
  }
}
