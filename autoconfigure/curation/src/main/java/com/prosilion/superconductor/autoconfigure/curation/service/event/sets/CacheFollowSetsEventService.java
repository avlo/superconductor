package com.prosilion.superconductor.autoconfigure.curation.service.event.sets;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeAwardReputationEventServiceIF;
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
    log.debug("materialize incomingFollowSetsEvent:\n  {}", incomingFollowSetsEvent.createPrettyPrintJson());
    GenericEventRecord genericEventRecord = incomingFollowSetsEvent.asGenericEventRecord();

    List<BadgeSetsEvent> badgeSetsEvents = getBadgeSetsEvents(genericEventRecord);

    FollowSetsEvent value = new FollowSetsEvent(
       genericEventRecord,
       badgeSetsEvents);

    return Optional.of(value);
  }

  private List<BadgeSetsEvent> getBadgeSetsEvents(GenericEventRecord genericEventRecord) {
    log.debug("... getBadgeSetsEvents for genericEventRecord ...");
    List<EventTag> eventTags = genericEventRecord.getTypeSpecificTags(EventTag.class);
    if (eventTags.isEmpty())
      throw new NostrException(String.format("FollowSetsEvent requires at least one EventTag:%n%s", genericEventRecord.createPrettyPrintJson()));

    List<BadgeSetsEvent> badgeSetsEvents = eventTags.stream()
       .map(eventTag ->
          getEvent(eventTag))
       .flatMap(Optional::stream).toList();

    if (badgeSetsEvents.isEmpty() || eventTags.size() != badgeSetsEvents.size()) {
      log.debug("eventTags.size != badgeSetsEvent.size");
      log.debug("eventTags:\n  [{}]", eventTags.stream().map(Record::toString).collect(Collectors.joining("], [")));
      log.debug("badgeSetsEvents:\n  [{}]", badgeSetsEvents.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining("], [")));
      throw new NostrException(
         String.format("eventTags.size [%d] != badgeSetsEvent.size [%d]", eventTags.size(), badgeSetsEvents.size()));
    }
    return badgeSetsEvents;
  }

  private Optional<BadgeSetsEvent> getEvent(EventTag eventTag) {
    log.debug("calling cacheBadgeSetsEventServiceIF.getEvent(eventTag) id: [{}] ...", eventTag.getEventId());
    Optional<BadgeSetsEvent> event = cacheBadgeSetsEventServiceIF.getEvent(
       eventTag.getEventId(),
       eventTag.requireRelay());
    log.debug("returning BadgeSetsEvent:\n{}",
       event.map(BadgeSetsEvent::createPrettyPrintJson).orElse("Optional.empty()"));
    return event;
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
             .getByDirect(
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
       .distinct()
       .toList();
  }

  @Override
  public List<FollowSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return materializeStream(cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag).stream()).toList();
  }

  @Override
  public Optional<FollowSetsEvent> getByDirect(@NonNull EventTag eventTag) {
    return cacheServiceIF.getFirstEventByKindAndEventTag(getKind(), eventTag).flatMap(this::materialize);
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
