package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
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
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheCurationSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.Collection;
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
  private final CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;
  private final CacheCurationSetsEventServiceIF cacheCurationSetsEventServiceIF;

  public CacheBadgeSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull Identity aImgIdentity,
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheReferenceAddressTagService,
     @NonNull CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheCurationSetsEventServiceIF cacheCurationSetsEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.aImgIdentity = aImgIdentity;
    this.cacheReferenceEventTagService = cacheReferenceEventTagService;
    this.cacheReferenceAddressTagService = cacheReferenceAddressTagService;
    this.cacheBadgeAwardGenericEventServiceIF = cacheBadgeAwardGenericEventServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
    this.cacheCurationSetsEventServiceIF = cacheCurationSetsEventServiceIF;
  }

  @Override
  public Optional<BadgeSetsEvent> materialize(@NonNull EventIF incomingBadgeSetsEvent) {
    log.debug("materialize(EventIF incomingBadgeSetsEvent):\n  {}", incomingBadgeSetsEvent.createPrettyPrintJson());
    Optional<GenericEventRecord> incomingBadgeSetsEventGER = cacheReferenceEventTagService.getEvent(
       incomingBadgeSetsEvent.getId(), incomingBadgeSetsEvent.getRelayTag().map(RelayTag::getRelay).orElse(null));

    if (incomingBadgeSetsEventGER.isEmpty()) {
      throw new NostrException("BadgeSetsEvent [%s] Optional returned EMPTY");
    }

    Relay incomingBadgeSetsEventRelay = incomingBadgeSetsEvent.requireFirstTag(RelayTag.class).getRelay();

    List<EventTag> eventTags = incomingBadgeSetsEventGER.map(e ->
       e.getTypeSpecificTags(EventTag.class)).stream().flatMap(Collection::stream).toList();
    log.debug("found event tags: [{}]", eventTags);

    List<CurationSetsEvent> curationSetsEventList = eventTags.stream().map(eventTag ->
       cacheCurationSetsEventServiceIF
          .getEvent(
             eventTag.getEventId(),
             new Relay(eventTag.requireRecommendedRelayUrl()))
          .stream()).flatMap(Stream::distinct).toList();
    log.debug("badgeSetsEventGER found curationSetsEventList: [{}]", curationSetsEventList);

    Optional<BadgeDefinitionReputationEvent> badgeDefinitionReputationEventOpts = cacheBadgeDefinitionReputationEventServiceIF.getBy(
       new AddressTag(
          Kind.BADGE_DEFINITION_EVENT,
          incomingBadgeSetsEvent.requireFirstTag(PubKeyTag.class).getPublicKey(),
          incomingBadgeSetsEvent.requireFirstTag(IdentifierTag.class),
          incomingBadgeSetsEvent.getRelayTag().map(RelayTag::getRelay).orElse(null)));
    log.debug("badgeSetsEventGER found badgeDefinitionReputationEventOpts: [{}]",
       badgeDefinitionReputationEventOpts.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n  ")));

    Optional<BadgeSetsEvent> badgeSetsEvent = badgeDefinitionReputationEventOpts
       .map(badgeDefinitionReputationEvent ->
          new BadgeSetsEvent(
             aImgIdentity,
             badgeDefinitionReputationEvent,
             curationSetsEventList,
             incomingBadgeSetsEventRelay));

    log.debug("returning badgeSetsEvent: [{}]",
       badgeSetsEvent.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n  ")));

    return badgeSetsEvent;
  }

  @Override
  public Optional<BadgeSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    log.debug("inside getEvent(eventId, relay):\n  event [{}]\n  relay [{}]", eventId, relay);
    log.debug("calling cacheDereferenceEventTagService.getEvent(eventId, relay)...");
    Optional<GenericEventRecord> event = cacheReferenceEventTagService.getEvent(eventId, relay);
    log.debug("... cacheDereferenceEventTagService.getEvent(eventId, relay) returned:\n  [{}]",
       event.map(GenericEventRecord::createPrettyPrintJson));
    return event.flatMap(this::materialize);
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull AddressTag referencedAbstractEventTag) {
    log.debug("inside getBy(@NonNull AddressTag [{}]", referencedAbstractEventTag);
    log.debug("calling cacheReferenceAddressTagService.getBy(referencedAbstractEventTag)...");
    Optional<GenericEventRecord> event = cacheReferenceAddressTagService.getBy(referencedAbstractEventTag);
    log.debug("... calling cacheReferenceAddressTagService.getBy(referencedAbstractEventTag) returned:\n  [{}]",
       event.map(GenericEventRecord::createPrettyPrintJson));
    return event.flatMap(this::materialize);
  }

  @Override
  public List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag).stream()
       .map(this::materialize).flatMap(Optional::stream).toList();
  }

  @Override
  public List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    List<CurationSetsEvent> by = cacheCurationSetsEventServiceIF.getBy(pubKeyTag, eventTag);
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(getKind(), pubKeyTag, eventTag).stream()
       .map(this::materialize).flatMap(Optional::stream).toList();
  }

  @Override
  public List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag).stream()
       .map(this::materialize).flatMap(Optional::stream).toList();
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
