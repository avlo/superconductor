package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type.BadgeAwardReputationEventKindTypePlugin;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.DeleteEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FollowSetsEventKindPlugin extends PublishingEventKindPlugin { // kind 30_000
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final DeleteEventServiceIF deleteEventServiceIF;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;
  private final CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;
  private final BadgeSetsEventKindPlugin badgeSetsEventKindPlugin;
  private final BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin;
  private final CacheServiceIF cacheServiceIF;

  public FollowSetsEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull NotifierService notifierService,
     @NonNull EventPlugin eventPlugin,
     @NonNull DeleteEventServiceIF deleteEventServiceIF,
     @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF,
     @NonNull BadgeSetsEventKindPlugin badgeSetsEventKindPlugin,
     @NonNull BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin,
     @NonNull CacheServiceIF cacheServiceIF) {
    super(notifierService, eventPlugin);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.deleteEventServiceIF = deleteEventServiceIF;
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
    this.badgeSetsEventKindPlugin = badgeSetsEventKindPlugin;
    this.badgeAwardReputationEventKindTypePlugin = badgeAwardReputationEventKindTypePlugin;
    this.cacheServiceIF = cacheServiceIF;
    log.debug("using superconductorRelayUrl: [{}]", superconductorRelayUrl);
  }

  public Optional<GenericEventRecord> processIncomingEventUnvalidated(@NonNull FollowSetsEvent incomingFollowSetsEvent, @NonNull Relay fromRelay) {
    PubKeyTag recipientPubKeyTag = incomingFollowSetsEvent.requireFirstTag(PubKeyTag.class);
    AddressTag badgeDefinitionReputationEventAsAddressTag = incomingFollowSetsEvent.getBadgeSetsEventList().stream().map(BadgeSetsEvent::getBadgeDefinitionReputationEvent).findFirst().map(AddressableEvent::asAddressableEventAddressTag).orElseThrow();
    EventTag unvalidatedFollowSetsEventBadgeSetsEventAsEventTag = incomingFollowSetsEvent.requireFirstTag(EventTag.class);

    Optional<EventTag> badgeSetsEventAsEventTag =
       cacheBadgeSetsEventServiceIF.getBy(recipientPubKeyTag, badgeDefinitionReputationEventAsAddressTag).stream()
          .map(badgeSetsEvent ->
             new EventTag(badgeSetsEvent.getId(), badgeSetsEvent.getRelay().orElseThrow().getUrl())).findFirst();

    Optional<FollowSetsEvent> followSetsEventMatchingBadgeDefnRepEventOpt =
       badgeSetsEventAsEventTag.map(cacheFollowSetsEventServiceIF::getByDirect).stream().flatMap(Optional::stream).findFirst();

    if (followSetsEventMatchingBadgeDefnRepEventOpt.isEmpty()) {
      BadgeSetsEvent first = incomingFollowSetsEvent.getBadgeSetsEventList().getFirst();
//      cacheServiceIF.save(first);
      GenericEventRecord badgeSetsEventGER = badgeSetsEventKindPlugin.processIncomingEvent(first, fromRelay).orElseThrow();
      BadgeSetsEvent materializedBadgeSetsEvent = cacheBadgeSetsEventServiceIF.materialize(badgeSetsEventGER).orElseThrow();

      FollowSetsEvent newFromExisting = new FollowSetsEvent(
         superconductorInstanceIdentity,
         materializedBadgeSetsEvent,
         new Relay(superconductorRelayUrl));

      Optional<GenericEventRecord> genericEventRecord = processIncomingEvent(newFromExisting, fromRelay);
      return genericEventRecord;
    }

    FollowSetsEvent dbFollowSetsEvent = followSetsEventMatchingBadgeDefnRepEventOpt.get();

    CuratedBadgeAwardGenericEvent incomingFollowSetsEventCuratedBadgeAwardEvent = incomingFollowSetsEvent.getBadgeSetsEventList().getFirst().getCuratedBadgeAwardGenericEventList().getFirst();

    List<BadgeSetsEvent> followSetsEventMatchingBadgeDefnRepList = dbFollowSetsEvent.getBadgeSetsEventList();

    Optional<BadgeSetsEvent> foundMatchingBadgeSetsEventAlreadyContainingCuratedBadgeAwardEventOpt = followSetsEventMatchingBadgeDefnRepList.stream()
       .filter(badgeSetsEvent ->
          badgeSetsEvent.getCuratedBadgeAwardGenericEventList()
             .contains(incomingFollowSetsEventCuratedBadgeAwardEvent))
       .findFirst();

    if (foundMatchingBadgeSetsEventAlreadyContainingCuratedBadgeAwardEventOpt.isPresent()) {
      return Optional.of(dbFollowSetsEvent.asGenericEventRecord());
    }

    BadgeSetsEvent foundMatchingBadgeSetsEventThatDoesntYetContainCuratedBadgeAwardEvent =
       followSetsEventMatchingBadgeDefnRepList.stream().filter(badgeSetsEvent ->
          badgeSetsEvent.getBadgeDefinitionReputationEvent().equals(
             incomingFollowSetsEvent.getBadgeSetsEventList().getFirst().getBadgeDefinitionReputationEvent())).findFirst().orElseThrow();

    List<CuratedBadgeAwardGenericEvent> newCuratedAwardList =
       Stream.concat(
          foundMatchingBadgeSetsEventThatDoesntYetContainCuratedBadgeAwardEvent.getCuratedBadgeAwardGenericEventList().stream(),
          Stream.of(incomingFollowSetsEventCuratedBadgeAwardEvent)).toList();

    BadgeSetsEvent badgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       foundMatchingBadgeSetsEventThatDoesntYetContainCuratedBadgeAwardEvent.getBadgeDefinitionReputationEvent(),
       newCuratedAwardList,
       new Relay(superconductorRelayUrl));

    GenericEventRecord savedBadgeSetsEvent = badgeSetsEventKindPlugin.processIncomingEvent(badgeSetsEvent, fromRelay).orElseThrow();

    BadgeSetsEvent event =
       cacheBadgeSetsEventServiceIF.getEvent(savedBadgeSetsEvent.getId(), savedBadgeSetsEvent.getRelayTag().orElseThrow().getRelay())
          .orElseThrow();

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
       superconductorInstanceIdentity,
       event,
       new Relay(superconductorRelayUrl));
//    deletePrevious(foundMatchingBadgeSetsEventThatDoesntYetContainCuratedBadgeAwardEvent);
//    cacheServiceIF.save(badgeSetsEvent);
    Optional<GenericEventRecord> genericEventRecord = processIncomingEvent(followSetsEvent, fromRelay);

    return genericEventRecord;
  }

  //  TODO: examine re-arch of EventPluginIF such that processIncomingEvent(...) returns FollowSetsEvent instead of GenericEventRecord  
  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFollowSetsEvent, @NonNull Relay fromRelay) {
    log.debug("processing incoming FollowSetsEvent\n{}", incomingFollowSetsEvent.createPrettyPrintJson());
    Optional<FollowSetsEvent> existingEvent = cacheFollowSetsEventServiceIF.getEvent(
       incomingFollowSetsEvent.getId(),
       incomingFollowSetsEvent.getRelayTag().map(RelayTag::getRelay).orElseThrow());
    if (existingEvent.isPresent()) return existingEvent.map(BaseEvent::getGenericEventRecord);

    log.debug("(1of9) getting incomingFollowSetsEvent's BadgeSetsEventList...");
    Map<EventTag, BadgeSetsEvent> followSetsEventBadgeSetsEvent =
       incomingFollowSetsEvent.getTypeSpecificTags(EventTag.class).stream()
          .collect(
             Collectors.toMap(
                Function.identity(),
                eventTag -> cacheBadgeSetsEventServiceIF.getEvent(eventTag.getEventId(), eventTag.requireRelay()).orElseThrow()));

    List<BadgeSetsEvent> incomingFollowSetBadgeSetsEvents = followSetsEventBadgeSetsEvent.values().stream().toList();

    log.debug("... (3of9) filtering pre-existing matching BadgeSetsEvents ...");
    List<BadgeSetsEvent> filteredBadgeSetsEventList = incomingFollowSetBadgeSetsEvents.stream()
       .flatMap(badgeSetsEvent ->
          processIncomingBadgeSetsEventList(fromRelay, badgeSetsEvent))
       .flatMap(genericEventRecord ->
          cacheBadgeSetsEventServiceIF
             .getEvent(genericEventRecord.getId(), genericEventRecord.getRelayTag().orElseThrow().getRelay())
             .stream()).toList();
    log.debug("... (4of9) filteredBadgeSetsEventList contains:\n{}",
       filteredBadgeSetsEventList.stream().map(BadgeSetsEvent::createPrettyPrintJson).collect(Collectors.joining(",\n")));

    FollowSetsEvent materializedFollowSetsEvent = new FollowSetsEvent(
       superconductorInstanceIdentity,
       filteredBadgeSetsEventList,
       new Relay(superconductorRelayUrl));
    log.debug("(5of9) materialized new FollowSetsEvent:\n{}", materializedFollowSetsEvent.createPrettyPrintJson());

    log.debug("(7of9) ... calling super.processIncomingEvent(materializedFollowSetsEvent) ...");
    super.processIncomingEvent(materializedFollowSetsEvent, new Relay(superconductorRelayUrl));

    findAwardRecipientExistingFollowSets(materializedFollowSetsEvent).ifPresent(followSetsEvent -> {
      log.debug("(6of9) ... deleting previous existingDbFollowSets ...");
      deletePrevious(followSetsEvent);
    });

    log.debug("(8of9) ... calling badgeAwardReputationEventKindTypePlugin.processIncomingEvent(...) ...");
    badgeAwardReputationEventKindTypePlugin.processIncomingEvent(materializedFollowSetsEvent, fromRelay);

    log.debug("(9of9) ... done.  returning materializedFollowSetsEvent.asGenericEventRecord():\n  {}",
       materializedFollowSetsEvent.createPrettyPrintJson());
    return Optional.of(materializedFollowSetsEvent.asGenericEventRecord());
  }

  private @NonNull Stream<GenericEventRecord> processIncomingBadgeSetsEventList(@NonNull Relay fromRelay, BadgeSetsEvent badgeSetsEvent) {
    Optional<GenericEventRecord> genericEventRecord = badgeSetsEventKindPlugin.processIncomingEvent(badgeSetsEvent, fromRelay);
    return genericEventRecord.stream();
  }

  private Optional<GenericEventRecord> findAwardRecipientExistingFollowSets(FollowSetsEvent followSetsEvent) {
    PublicKey awardRecipientPublicKey = followSetsEvent.getAwardRecipientPublicKey();
    PubKeyTag pubKeyTag = new PubKeyTag(awardRecipientPublicKey);
    List<FollowSetsEvent> existingFollowSetsEventOpt = cacheFollowSetsEventServiceIF.getBy(pubKeyTag).stream().toList();
    List<FollowSetsEvent> filteredOutNewIncomingFollowSet = existingFollowSetsEventOpt.stream().filter(Predicate.not(followSetsEvent::equals)).toList();

    if (filteredOutNewIncomingFollowSet.size() > 1)
      throw new NostrException("found more than one follow sets for a given recipient");
    if (filteredOutNewIncomingFollowSet.isEmpty())
      log.debug("no existing FollowSetsEvent found for recipient [{}], return Optional.empty()", awardRecipientPublicKey);

    return filteredOutNewIncomingFollowSet.stream().findFirst().map(FollowSetsEvent::asGenericEventRecord);
  }

  private void deletePrevious(EventIF eventIF) {
//    deleteEventServiceIF.processIncomingEvent(eventIF, new Relay(superconductorRelayUrl));
    cacheServiceIF.deleteEvent(
       new DeletionEvent(
          superconductorInstanceIdentity,
          new EventTag(eventIF.getId(), eventIF.getRelayTag().orElseThrow().getRelay().getUrl()),
          "Delete from FollowSetsEventKindPlugin",
          new Relay(superconductorRelayUrl)));
    log.debug("debug for breakpoint, check DB contents");
  }

  @Override
  public Kind getKind() {
    log.debug("getKind Kind[{}]: {}",
       Kind.FOLLOW_SETS.getValue(),
       Kind.FOLLOW_SETS.getName().toUpperCase());
    return Kind.FOLLOW_SETS;
  }
}
