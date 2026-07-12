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
    GenericEventRecord genericEventRecord = incomingFollowSetsEvent.asGenericEventRecord();
    return Optional.of(
       new FollowSetsEvent(
          genericEventRecord,
          getBadgeSetsEvents(
             genericEventRecord)));
  }

  private List<BadgeSetsEvent> getBadgeSetsEvents(GenericEventRecord genericEventRecord) {
    List<EventTag> eventTags = genericEventRecord.getTypeSpecificTags(EventTag.class);
    if (eventTags.isEmpty())
      throw new NostrException(String.format("FollowSetsEvent [%s] requires at least one EventTag", genericEventRecord));

    List<BadgeSetsEvent> badgeSetsEvents = eventTags.stream()
       .map(eventTag ->
          cacheBadgeSetsEventServiceIF.getEvent(
             eventTag.getEventId(),
             eventTag.requireRelay()))
       .flatMap(Optional::stream).toList();

    if (eventTags.size() != badgeSetsEvents.size()) {
      log.debug("eventTags.size != badgeSetsEvent.size");
      log.debug("eventTags:\n  [{}]", eventTags.stream().map(Record::toString).collect(Collectors.joining("], [")));
      log.debug("badgeSetsEvents:\n  [{}]", badgeSetsEvents.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining("], [")));
      throw new NostrException("eventTags.size != badgeSetsEvent.size");
    }
    return badgeSetsEvents;
  }

  @Override
  public Optional<FollowSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheReferenceEventTagServiceIF.getEvent(eventId, relay).flatMap(this::materialize);
  }

  @Override
  public List<BadgeAwardReputationEvent> getBadgeAwardReputationEvents(@NonNull FollowSetsEvent followSetsEvent) {
    PubKeyTag awardRecipient = new PubKeyTag(followSetsEvent.getAwardRecipientPublicKey());
    return followSetsEvent.getBadgeSetsEventList().stream()
       .flatMap(badgeSetsEvent ->
          cacheKindAddressTagServiceIF
             .getBy(
                Kind.BADGE_AWARD_EVENT,
                awardRecipient,
                badgeSetsEvent.getBadgeDefinitionReputationEvent()
                   .asAddressableEventAddressTag())
             .stream())
       .<BadgeAwardReputationEvent>mapMulti(
          (event, badgeAwardReputationEventConsumer) ->
             cacheBadgeAwardReputationEventServiceIF
                .getEvent(
                   event.getId(),
                   event.requireFirstTag(RelayTag.class).getRelay())
                .ifPresent(badgeAwardReputationEventConsumer))
       .toList();
  }

  @Override
  public List<FollowSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return materializeList(cacheServiceIF.getEventsByKindAndPubKeyTagAndAddressTag(getKind(), pubKeyTag, addressTag)).toList();
  }

  @Override
  @Deprecated
  public Optional<FollowSetsEvent> getBy(@NonNull EventTag eventTag) {
    return cacheReferenceEventTagServiceIF.getBy(eventTag).flatMap(this::materialize);
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
