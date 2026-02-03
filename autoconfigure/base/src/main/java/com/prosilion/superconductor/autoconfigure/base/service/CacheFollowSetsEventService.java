package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
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
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheFollowSetsEventService implements CacheFollowSetsEventServiceIF {
  public static final String NON_EXISTENT_EVENT_ID_S = "FollowSetsEvent [%s] contains AddressTag [%s] referencing non-existent BadgeDefinitionReputationEvent";
  private final String superconductorRelayUrl;
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheBadgeAwardGenericEventServiceIF cacheBadgeGenericAwardEventServiceIF;

  public CacheFollowSetsEventService(
      @NonNull String superconductorRelayUrl,
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF) {
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheBadgeGenericAwardEventServiceIF = cacheBadgeAwardGenericEventServiceIF;
  }

  @Override
  public Optional<FollowSetsEvent> getEvent(@NonNull String eventId, @NonNull String url) {
//  TODO: follow sets event should not exist without at least single event tag, but doesn't necessarily break any logic.  needs follow up    
//    if (eventTagsOfFollowSetsEvent.size() != 1)
//      throw new NostrException(
//          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", unpopulatedFollowSetsEvent, eventTagsOfFollowSetsEvent.size()));    
    Optional<GenericEventRecord> unpopulatedFollowSetsEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedFollowSetsEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedFollowSetsEvent.get()));
  }

  @Override
  public FollowSetsEvent materialize(@NonNull GenericEventRecord incomingFollowSetsEvent) {
    List<EventTag> eventTagsOfFollowSetsEvent = Filterable.getTypeSpecificTags(EventTag.class, incomingFollowSetsEvent).stream().toList();

    if (eventTagsOfFollowSetsEvent.isEmpty())
      throw new NostrException(
          String.format("FollowSetsEvent [%s] requires at least one EventTag", incomingFollowSetsEvent));

    Map<GenericEventRecord, String> badgeAwardEventsAsGenericEventRecords =
        eventTagsOfFollowSetsEvent.stream()
            .collect(
                Collectors.toMap(eventTag ->
                    getCacheDereferenceEventTagEvent(eventTag).orElseThrow(), EventTag::getRecommendedRelayUrl));

    List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardAbstractEvents = badgeAwardEventsAsGenericEventRecords
        .entrySet()
        .stream()
        .map(entry ->
            getCacheBadgeGenericAwardEvent(entry.getKey(), entry.getValue()))
        .flatMap(Optional::stream)
        .toList();

    Function<EventTag, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> fxn = eventTag ->
        badgeAwardAbstractEvents.stream()
            .filter(badgeAwardGenericEvent ->
                badgeAwardGenericEvent.getId().equals(eventTag.getIdEvent())).findFirst().orElseThrow();

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
        incomingFollowSetsEvent.asGenericEventRecord(), fxn);

    return followSetsEvent;
  }

  private Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getCacheBadgeGenericAwardEvent(GenericEventRecord cacheBadgeGenericAwardEventId, String url) {
    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> event = cacheBadgeGenericAwardEventServiceIF.getEvent(cacheBadgeGenericAwardEventId.getId(), url);
    return event;
  }

  private Optional<GenericEventRecord> getCacheDereferenceEventTagEvent(EventTag cacheDereferenceEvent) {
    Optional<GenericEventRecord> event = cacheDereferenceEventTagServiceIF.getEvent(cacheDereferenceEvent);
    return event;
  }

  @Override
  public List<FollowSetsEvent> getEventsByPubkeyTag(@NonNull PublicKey badgeAwardRecipientPublicKey) {
    List<GenericEventRecord> eventsByKindAndPubKeyTag = cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), badgeAwardRecipientPublicKey);

    List<String> eventIds = eventsByKindAndPubKeyTag.stream()
        .map(GenericEventRecord::id).toList();

    List<FollowSetsEvent> list = eventIds.stream()
        .map(eventId -> getEvent(eventId, superconductorRelayUrl)).flatMap(Optional::stream).toList();

    return list;
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
