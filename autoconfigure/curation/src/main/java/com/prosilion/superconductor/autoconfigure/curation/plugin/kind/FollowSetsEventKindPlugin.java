package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
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
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FollowSetsEventKindPlugin extends PublishingEventKindPlugin { // kind 30_000
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;
  private final CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final BadgeSetsEventKindPlugin badgeSetsEventKindPlugin;
  private final BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin;
  private final CacheServiceIF cacheServiceIF;

  public FollowSetsEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull NotifierService notifierService,
     @NonNull EventPlugin eventPlugin,
     @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull BadgeSetsEventKindPlugin badgeSetsEventKindPlugin,
     @NonNull BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin,
     @NonNull CacheServiceIF cacheServiceIF) {
    super(notifierService, eventPlugin);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
    this.badgeSetsEventKindPlugin = badgeSetsEventKindPlugin;
    this.badgeAwardReputationEventKindTypePlugin = badgeAwardReputationEventKindTypePlugin;
    this.cacheServiceIF = cacheServiceIF;
    log.debug("using superconductorRelayUrl: [{}]", superconductorRelayUrl);
  }

  public Optional<GenericEventRecord> processIncomingCuratedBadgeAwardGenericEvent(@NonNull CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent, @NonNull Relay fromRelay) {
    PublicKey recipientPublicKey = curatedBadgeAwardGenericEvent.getAwardRecipientPublicKey();
    List<BadgeDefinitionReputationEvent> matchingBadgeDefinitionReputationEventList = cacheBadgeDefinitionReputationEventServiceIF.findByMatching(curatedBadgeAwardGenericEvent);

    List<BadgeSetsEvent> recipientBadgeSetsEventListThatMatchBadgeDefinitionReputationEvent =
       matchingBadgeDefinitionReputationEventList.stream().map(AddressableEvent::asAddressableEventAddressTag).map(addressTag ->
          cacheBadgeSetsEventServiceIF.getBy(
             new PubKeyTag(recipientPublicKey),
             addressTag)).flatMap(Optional::stream).toList();

    return processIncomingFollowSetsBadgeSetsEvents(recipientBadgeSetsEventListThatMatchBadgeDefinitionReputationEvent, fromRelay);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFollowSetsEvent, @NonNull Relay fromRelay) {
    log.debug("processing incoming FollowSetsEvent\n{}", incomingFollowSetsEvent.createPrettyPrintJson());
    Optional<FollowSetsEvent> existingEvent = cacheFollowSetsEventServiceIF.getEvent(
       incomingFollowSetsEvent.getId(),
       incomingFollowSetsEvent.getRelayTag().map(RelayTag::getRelay).orElseThrow());
    if (existingEvent.isPresent()) return existingEvent.map(BaseEvent::getGenericEventRecord);

    PubKeyTag recipientPubKeyTag = incomingFollowSetsEvent.getTypeSpecificTags(PubKeyTag.class).getFirst();
    log.debug("(1of9) getting incomingFollowSetsEvent's BadgeSetsEventList...");
    Map<AddressTag, BadgeSetsEvent> reconstructedFollowSetsEventBadgeSetsEvents =
       incomingFollowSetsEvent.getTypeSpecificTags(AddressTag.class).stream()
          .collect(
             Collectors.toMap(
                Function.identity(),
                addressTag -> cacheBadgeSetsEventServiceIF.getBy(
                   recipientPubKeyTag, addressTag.getIdentifierTag()).orElseThrow()));
    
    List<BadgeSetsEvent> incomingFollowSetBadgeSetsEvents = reconstructedFollowSetsEventBadgeSetsEvents.values().stream().toList();
    return processIncomingFollowSetsBadgeSetsEvents(incomingFollowSetBadgeSetsEvents, fromRelay);
  }

  private Optional<GenericEventRecord> processIncomingFollowSetsBadgeSetsEvents(List<BadgeSetsEvent> incomingFollowSetBadgeSetsEvents, Relay fromRelay) {
    log.debug("... (3of9) filtering pre-existing matching BadgeSetsEvents ...");
    List<BadgeSetsEvent> filteredBadgeSetsEventList = incomingFollowSetBadgeSetsEvents.stream()
       .flatMap(badgeSetsEvent ->
          badgeSetsEventKindPlugin.processIncomingEvent(badgeSetsEvent, fromRelay).stream())
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

    log.debug("(6of9) ... calling super.processIncomingEvent(materializedFollowSetsEvent) ...");
    super.processIncomingEvent(materializedFollowSetsEvent, new Relay(superconductorRelayUrl));

    log.debug("(8of9) ... calling badgeAwardReputationEventKindTypePlugin.processIncomingEvent(...) ...");
    badgeAwardReputationEventKindTypePlugin.processIncomingEvent(materializedFollowSetsEvent, fromRelay);

    findAwardRecipientExistingFollowSets(materializedFollowSetsEvent).ifPresent(followSetsEvent -> {
      log.debug("(7.1of9) ... deleting previous existingDbFollowSets ...");
      log.debug("(7.2of9) ... first, delete previous FollowSetsEvent's BadgeSetsEvent(s) ...");
      followSetsEvent.getBadgeSetsEventList().forEach(this::deletePrevious);
      log.debug("(7.3of9) ... finally, delete the previous existingDbFollowSetsEvent itself ...");
      deletePrevious(followSetsEvent);
      log.debug("(7.4of9) ... done");
    });

    log.debug("(9of9) ... done.  returning materializedFollowSetsEvent.asGenericEventRecord():\n  {}",
       materializedFollowSetsEvent.createPrettyPrintJson());
    return Optional.of(materializedFollowSetsEvent.asGenericEventRecord());
  }

  private Optional<FollowSetsEvent> findAwardRecipientExistingFollowSets(FollowSetsEvent followSetsEvent) {
    List<FollowSetsEvent> filteredOutNewIncomingFollowSet =
       cacheFollowSetsEventServiceIF.getBy(
             new PubKeyTag(followSetsEvent.getAwardRecipientPublicKey())).stream().filter(Predicate.not(followSetsEvent::equals))
          .toList();

    if (filteredOutNewIncomingFollowSet.size() > 1)
      throw new NostrException("found more than one follow sets for a given recipient");
    if (filteredOutNewIncomingFollowSet.isEmpty())
      log.debug("no existing FollowSetsEvent found for recipient [{}], return Optional.empty()", followSetsEvent.getAwardRecipientPublicKey());

    return filteredOutNewIncomingFollowSet.stream().findFirst();
  }

  private void deletePrevious(EventIF eventIF) {
    log.debug("inside deletePrevious(EventIF), calling cacheServiceIF.deleteEvent(...) for event:\n {}", eventIF.createPrettyPrintJson());
//    deleteEventServiceIF.processIncomingEvent(eventIF, new Relay(superconductorRelayUrl));
    cacheServiceIF.deleteEvent(
       new DeletionEvent(
          superconductorInstanceIdentity,
          new EventTag(eventIF.getId(), eventIF.getRelayTag().orElseThrow().getRelay().getUrl()),
          "Delete from FollowSetsEventKindPlugin",
          new Relay(superconductorRelayUrl)));
    log.debug("done.");
  }

  @Override
  public Kind getKind() {
    log.debug("getKind Kind[{}]: {}",
       Kind.FOLLOW_SETS.getValue(),
       Kind.FOLLOW_SETS.getName().toUpperCase());
    return Kind.FOLLOW_SETS;
  }
}
