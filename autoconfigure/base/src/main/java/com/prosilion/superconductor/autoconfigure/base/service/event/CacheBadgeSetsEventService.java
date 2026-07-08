package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.BadgeSetsEvent;
import com.prosilion.nostr.event.CurationSetsEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheCurationSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeSetsEventService implements CacheBadgeSetsEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final Identity aImgIdentity;
  private final CacheReferenceEventTagService cacheReferenceEventTagService;
  private final CacheReferenceAddressTagService cacheReferenceAddressTagService;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheCurationSetsEventServiceIF cacheCurationSetsEventServiceIF;

  public CacheBadgeSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull Identity aImgIdentity,
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheReferenceAddressTagService,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheCurationSetsEventServiceIF cacheCurationSetsEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.aImgIdentity = aImgIdentity;
    this.cacheReferenceEventTagService = cacheReferenceEventTagService;
    this.cacheReferenceAddressTagService = cacheReferenceAddressTagService;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
    this.cacheCurationSetsEventServiceIF = cacheCurationSetsEventServiceIF;
  }

  @Override
  public Optional<BadgeSetsEvent> materialize(@NonNull EventIF incomingBadgeSetsEvent) {
    log.debug("materialize(EventIF incomingBadgeSetsEvent):\n  {}", incomingBadgeSetsEvent.createPrettyPrintJson());

    List<EventTag> eventTags = incomingBadgeSetsEvent.getTypeSpecificTags(EventTag.class);
    List<CurationSetsEvent> curationSetsEventList = eventTags.stream().map(eventTag ->
       cacheCurationSetsEventServiceIF
          .getEvent(eventTag.eventId(), eventTag.requireRelay())
          .stream()).flatMap(Stream::distinct).toList();
    log.debug("cacheCurationSetsEventServiceIF.getBy(eventTag) found curationSetsEventList: [{}]", curationSetsEventList);

    AddressTag eventAddressTag = incomingBadgeSetsEvent.requireFirstTag(AddressTag.class);
    IdentifierTag identifierTag = incomingBadgeSetsEvent.requireFirstTag(IdentifierTag.class);
    Optional<BadgeDefinitionReputationEvent> badgeDefinitionReputationEventOpt =
       cacheBadgeDefinitionReputationEventServiceIF.getBy(eventAddressTag, new PubKeyTag(new PublicKey(identifierTag.getUuid())));
    log.debug("badgeSetsEventGER found badgeDefinitionReputationEventOpt: [{}]",
       badgeDefinitionReputationEventOpt.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n  ")));

    Relay incomingBadgeSetsEventRelay = incomingBadgeSetsEvent.requireFirstTag(RelayTag.class).getRelay();
    Optional<BadgeSetsEvent> badgeSetsEvent = badgeDefinitionReputationEventOpt
       .map(badgeDefinitionReputationEvent ->
       {
         BadgeSetsEvent badgeSetsEvent1 = new BadgeSetsEvent(
            incomingBadgeSetsEvent.asGenericEventRecord(),
            badgeDefinitionReputationEvent,
            curationSetsEventList);
         return badgeSetsEvent1;
       });

    log.debug("returning badgeSetsEvent: [{}]",
       badgeSetsEvent.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n  ")));

    return badgeSetsEvent;
  }

  @Override
  public Optional<BadgeSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    log.debug("inside getEvent(eventId, relay):\n  event [{}]\n  relay [{}]", eventId, relay);
    log.debug("calling cacheDereferenceEventTagService.getEvent(eventId, relay)...");
    Optional<GenericEventRecord> event = cacheServiceIF.getEventByEventId(eventId);
    log.debug("... cacheDereferenceEventTagService.getEvent(eventId, relay) returned:\n  [{}]",
       event.map(GenericEventRecord::createPrettyPrintJson));
    return event.flatMap(this::materialize);
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull AddressTag referencedAbstractEventTag) {
    log.debug("inside getBy(@NonNull AddressTag [{}]", referencedAbstractEventTag);
    log.debug("calling cacheReferenceAddressTagService.getBy(referencedAbstractEventTag)...");
    Optional<BadgeSetsEvent> event = cacheKindAddressTagServiceIF.getBy(getKind(), referencedAbstractEventTag).stream().map(this::materialize).flatMap(Optional::stream).findFirst();
    log.debug("... calling cacheReferenceAddressTagService.getBy(referencedAbstractEventTag) returned:\n  [{}]",
       event.map(EventIF::createPrettyPrintJson));
    return event;
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag referencedAbstractEventTag) {
    log.debug("inside getBy(@NonNull AddressTag [{}]", referencedAbstractEventTag);
    log.debug("calling cacheReferenceAddressTagService.getBy(referencedAbstractEventTag)...");
    Optional<BadgeSetsEvent> event = cacheKindAddressTagServiceIF.getBy(
       getKind(), pubKeyTag, referencedAbstractEventTag).stream().map(this::materialize).flatMap(Optional::stream).findFirst();
    log.debug("... calling cacheReferenceAddressTagService.getBy(referencedAbstractEventTag) returned:\n  [{}]",
       event.map(EventIF::createPrettyPrintJson));
    return event;
  }

  @Override
  public List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag).stream()
       .map(this::materialize).flatMap(Optional::stream).toList();
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(getKind(), pubKeyTag, eventTag).stream()
       .map(this::materialize).flatMap(Optional::stream).findFirst();
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag).stream()
       .map(this::materialize).flatMap(Optional::stream).findFirst();
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
