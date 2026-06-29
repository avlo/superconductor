package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEventAux;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.BadgeGenericEventAux;
import com.prosilion.nostr.event.BadgeSetsEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.tag.SetsPairedEvents;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventAuxServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventAuxServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeSetsEventService implements CacheBadgeSetsEventServiceIF {
  private final CacheReferenceEventTagService cacheDereferenceEventTagService;
  private final CacheReferenceAddressTagService cacheReferenceAddressTagService;
  private final CacheBadgeAwardGenericEventAuxServiceIF cacheBadgeAwardGenericEventAuxServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheBadgeDefinitionGenericEventAuxServiceIF cacheBadgeDefinitionGenericEventAuxServiceIF;

  public CacheBadgeSetsEventService(
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheReferenceAddressTagService,
     @NonNull CacheBadgeAwardGenericEventAuxServiceIF cacheBadgeAwardGenericEventAuxServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventAuxServiceIF cacheBadgeDefinitionGenericEventAuxServiceIF) {
    this.cacheDereferenceEventTagService = cacheDereferenceEventTagService;
    this.cacheReferenceAddressTagService = cacheReferenceAddressTagService;
    this.cacheBadgeAwardGenericEventAuxServiceIF = cacheBadgeAwardGenericEventAuxServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
    this.cacheBadgeDefinitionGenericEventAuxServiceIF = cacheBadgeDefinitionGenericEventAuxServiceIF;
  }

  @Override
  public Optional<BadgeSetsEvent> materialize(@NonNull EventIF incomingBadgeSetsEvent) {
    log.debug("materialize(EventIF incomingBadgeSetsEvent):\n  {}", incomingBadgeSetsEvent.createPrettyPrintJson());
    Optional<GenericEventRecord> incomingBadgeSetsEventGER = cacheDereferenceEventTagService.getEvent(
       incomingBadgeSetsEvent.getId(), incomingBadgeSetsEvent.getRelayTag().orElseThrow().getRelay());

    if (incomingBadgeSetsEventGER.isEmpty()) {
      throw new NostrException("BadgeSetsEvent [%s] Optional returned EMPTY");
    }

    List<EventTag> eventTags = incomingBadgeSetsEventGER.map(e ->
       e.getTypeSpecificTags(EventTag.class)).stream().flatMap(Collection::stream).toList();

    log.debug("found event tags: [{}]", eventTags);

    List<BadgeAwardGenericEventAux> badgeAwardGenericEventAuxes = eventTags.stream().map(eventTag ->
       cacheBadgeAwardGenericEventAuxServiceIF
          .getEvent(
             eventTag.getIdEvent(),
             new Relay(eventTag.requireRecommendedRelayUrl()))
          .stream()).flatMap(Stream::distinct).toList();

    log.debug("badgeSetsEventGER found badgeAwardGenericEventAuxes: [{}]", badgeAwardGenericEventAuxes);

    if (badgeAwardGenericEventAuxes.stream().map(BadgeGenericEventAux::getPublicKey).distinct().count() > 1)
      throw new NostrException("badgeSetsEventGER event auxes contained different public keys");

    PublicKey eventAuxPubkey = badgeAwardGenericEventAuxes.stream().map(BadgeGenericEventAux::getPublicKey).findFirst().orElseThrow();

    List<AddressTag> addressTags = incomingBadgeSetsEventGER.map(e ->
       e.getTypeSpecificTags(AddressTag.class)).stream().flatMap(Collection::stream).toList();

    log.debug("badgeSetsEventGER found event tags: [{}]", eventTags);

    if (addressTags.size() != eventTags.size())
      throw new NostrException(
         String.format("uneven eventTags size [%s] vs addressTags size [%s]", eventTags.size(), addressTags.size()));

    List<SetsPairedEvents> setsPairedEvents = IntStream.range(0, eventTags.size()).mapToObj(operand ->
       new SetsPairedEvents(
          addressTags.get(operand), eventTags.get(operand), eventAuxPubkey)).toList();

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
             incomingBadgeSetsEventGER.get(),
             setsPairedEvents,
             badgeDefinitionReputationEvent));

    log.debug("returning badgeSetsEvent: [{}]",
       badgeSetsEvent.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n  ")));

    return badgeSetsEvent;
  }

  @Override
  public Optional<BadgeSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    log.debug("inside getEvent(eventId, relay):\n  event [{}]\n  relay [{}]", eventId, relay);
    log.debug("calling cacheDereferenceEventTagService.getEvent(eventId, relay)...");
    Optional<GenericEventRecord> event = cacheDereferenceEventTagService.getEvent(eventId, relay);
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
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
