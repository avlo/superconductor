package com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.UniqueAddressTagEvent;
import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.ReputationCalculationServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.DeleteEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.PublishingEventKindTypePlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardReputationEventKindTypePlugin extends PublishingEventKindTypePlugin {
  private final String superconductorRelayUrl;
  private final Identity superconductorInstanceIdentity;
  private final CacheServiceIF cacheServiceIF;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;
  private final ReputationCalculationServiceIF reputationCalculationServiceIF;
  private final DeleteEventServiceIF deleteEventServiceIF;

  public BadgeAwardReputationEventKindTypePlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull NotifierService notifierService,
     @NonNull EventKindTypePluginIF eventKindTypePlugin,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull ReputationCalculationServiceIF reputationCalculationServiceIF,
     @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF,
     @NonNull DeleteEventServiceIF deleteEventServiceIF) {
    super(notifierService, eventKindTypePlugin);
    this.cacheServiceIF = cacheServiceIF;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.reputationCalculationServiceIF = reputationCalculationServiceIF;
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
    this.deleteEventServiceIF = deleteEventServiceIF;
    log.debug("using superconductorRelayUrl: [{}]", superconductorRelayUrl);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFollowSetsEventAsReputationEvent, @NonNull Relay relay) {
    log.debug("processing incoming incomingFollowSetsEventAsReputationEvent, Kind[{}]:{}\n{}",
       incomingFollowSetsEventAsReputationEvent.getKind().getValue(),
       incomingFollowSetsEventAsReputationEvent.getKind().getName().toUpperCase(),
       incomingFollowSetsEventAsReputationEvent.createPrettyPrintJson());

    FollowSetsEvent materializedIncomingFollowSetsEvent =
       cacheFollowSetsEventServiceIF.materialize(incomingFollowSetsEventAsReputationEvent).orElseThrow();
    log.debug("(0ofY) ... materializedIncomingFollowSetsEvent:\n{}", materializedIncomingFollowSetsEvent.createPrettyPrintJson());

    List<BadgeAwardReputationEvent> existingBadgeAwardReputationEvents =
       cacheFollowSetsEventServiceIF.getBadgeAwardReputationEvents(materializedIncomingFollowSetsEvent);

    List<BadgeAwardReputationEvent> newReputationEvents =
       updateReputationEvents(
          materializedIncomingFollowSetsEvent,
          existingBadgeAwardReputationEvents.stream()
             .collect(Collectors.toMap(
                UniqueAddressTagEvent::getAddressTag,
                Function.identity())));

    log.debug("(6ofY) ... newReputationEvent:\n  {}", newReputationEvents.stream().map(EventIF::createPrettyPrintJson));
//    TODO: possibly reverse order below
    existingBadgeAwardReputationEvents.forEach(this::deletePreviousBadgeAwardReputationEvent); // delete old

    List<GenericEventRecord> genericEventRecords = newReputationEvents.stream()
       .map(newReputationEvent ->
          super.processIncomingEvent(newReputationEvent, relay)).flatMap(Optional::stream).toList();

    return genericEventRecords.stream().findFirst();
  }

  private @NonNull List<BadgeAwardReputationEvent> updateReputationEvents(FollowSetsEvent followSetsEvent, Map<AddressTag, BadgeAwardReputationEvent> previousReputationEventsByDefinition) {
    return followSetsEvent.getBadgeSetsEventList().stream()
       .map(badgeSetsEvent ->
          Optional.ofNullable(
                previousReputationEventsByDefinition
                   .get(badgeSetsEvent.getBadgeDefinitionReputationEvent().asAddressableEventAddressTag()))
             .orElseGet(() ->
                createFreshBadgeAwardReputationEvent(followSetsEvent, badgeSetsEvent)))
       .map(previousReputationEvent ->
          calculateBadgeAwardReputationEvent(
             previousReputationEvent.getBadgeDefinitionEvent(),
             filterNewCuratedBadgeAwardGenericEvents(followSetsEvent, previousReputationEvent.getAddressTag()),
             previousReputationEvent))
       .toList();
  }

  BiFunction<Stream<BadgeSetsEvent>, AddressTag, List<CuratedBadgeAwardGenericEvent>> fxnDo =
     (badgeSetsEventStream, addressTag) ->
        badgeSetsEventStream.filter(badgeaa ->
              badgeaa.getBadgeDefinitionReputationEvent().asAddressableEventAddressTag().equals(addressTag)).findFirst()
           .map(BadgeSetsEvent::getCuratedBadgeAwardGenericEventList).stream()
           .flatMap(Collection::stream).toList();

  private List<CuratedBadgeAwardGenericEvent> filterNewCuratedBadgeAwardGenericEvents(FollowSetsEvent followSetsEvent, AddressTag addressTag) {
    return
       fxnDo.apply(
             followSetsEvent.getBadgeSetsEventList().stream(), addressTag).stream()
          .filter(e ->
             !fxnDo.apply(findAwardRecipientExistingFollowSets(followSetsEvent).stream()
                   .map(FollowSetsEvent::getBadgeSetsEventList).flatMap(Collection::stream), addressTag)
                .contains(e))
          .toList();
  }

  private Optional<FollowSetsEvent> findAwardRecipientExistingFollowSets(FollowSetsEvent followSetsEvent) {
    List<FollowSetsEvent> filteredOutNewIncomingFollowSet =
       cacheFollowSetsEventServiceIF.getBy(new PubKeyTag(followSetsEvent.getAwardRecipientPublicKey()))
          .stream().filter(Predicate.not(followSetsEvent::equals)).toList();

    if (filteredOutNewIncomingFollowSet.size() > 1)
      throw new NostrException("found more than one follow sets for a given recipient");
    if (filteredOutNewIncomingFollowSet.isEmpty())
      log.debug("no existing FollowSetsEvent found for recipient [{}], return Optional.empty()", followSetsEvent.getAwardRecipientPublicKey());

    return filteredOutNewIncomingFollowSet.stream().findFirst();
  }

  private BadgeAwardReputationEvent createFreshBadgeAwardReputationEvent(FollowSetsEvent followSetsEvent, BadgeSetsEvent badgeSetsEvent) {
    return createBadgeAwardReputationEvent(
       followSetsEvent.getAwardRecipientPublicKey(),
       badgeSetsEvent.getBadgeDefinitionReputationEvent(),
       BigDecimal.ZERO);
  }

  private BadgeAwardReputationEvent calculateBadgeAwardReputationEvent(
     BadgeDefinitionReputationEvent badgeDefinitionReputationEvent,
     List<CuratedBadgeAwardGenericEvent> curatedBadgeAwardGenericEventList,
     BadgeAwardReputationEvent previousReputationEvent) {
    return reputationCalculationServiceIF.calculateReputationEventRxR(
       previousReputationEvent.getAwardRecipientPublicKey(),
       previousReputationEvent,
       badgeDefinitionReputationEvent.getCuratedFormulaEvents(),
       curatedBadgeAwardGenericEventList);
  }

  private BadgeAwardReputationEvent createBadgeAwardReputationEvent(
     PublicKey badgeReceiverPubkey,
     BadgeDefinitionReputationEvent badgeDefinitionReputationEvent,
     BigDecimal score) {
    BadgeAwardReputationEvent badgeAwardReputationEvent = new BadgeAwardReputationEvent(
       superconductorInstanceIdentity,
       badgeReceiverPubkey,
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationEvent,
       score,
       new Relay(superconductorRelayUrl));
    return badgeAwardReputationEvent;
  }

  private void deletePreviousBadgeAwardReputationEvent(EventIF previousReputationEvent) {
    deleteEventServiceIF.processIncomingEvent(
       previousReputationEvent,
       previousReputationEvent.getRelayTag().orElseThrow().getRelay());
  }
}
