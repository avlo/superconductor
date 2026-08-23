package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedFormulaEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class UniversalVoteEventKindPlugin extends NonPublishingEventKindPlugin {
  private final CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF;
  private final CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;
  private final FollowSetsEventKindPlugin followSetsEventKindPlugin;
  private final BadgeSetsEventKindPlugin badgeSetsEventKindPlugin;
  private final Identity superconductorInstanceIdentity;
  private final Relay superconductorRelay;
  private final CacheServiceIF cacheServiceIF;

  public UniversalVoteEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventService,
     @NonNull CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF,
     @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF,
     @NonNull FollowSetsEventKindPlugin followSetsEventKindPlugin,
     @NonNull BadgeSetsEventKindPlugin badgeSetsEventKindPlugin,
     @NonNull EventPluginIF eventPluginIF,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF) {
    super(eventPluginIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardGenericEventServiceIF;
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventService;
    this.cacheCuratedFormulaEventServiceIF = cacheCuratedFormulaEventServiceIF;
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
    this.followSetsEventKindPlugin = followSetsEventKindPlugin;
    this.badgeSetsEventKindPlugin = badgeSetsEventKindPlugin;
    this.superconductorRelay = new Relay(superconductorRelayUrl);
    this.cacheServiceIF = cacheServiceIF;
    log.debug("using superconductorRelayUrl: [{}]", superconductorRelayUrl);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF voteEvent, @NonNull Relay fromRelay) {
    log.debug("processing incoming voteEvent\n{}", voteEvent.createPrettyPrintJson());
    Optional<CuratedBadgeAwardGenericEvent> existingVoteEvent = cacheCuratedBadgeAwardGenericEventServiceIF.getByDirect(
       new EventTag(voteEvent.getId(),
          voteEvent.getRelayTag().map(RelayTag::getRelay).map(Relay::getUrl).orElseThrow()));
    if (existingVoteEvent.isPresent()) {
      log.debug("returning existing matching voteEvent");
      return existingVoteEvent.map(BaseEvent::getGenericEventRecord);
    }

    PubKeyTag recipientPublicKeyAsPubKeyTag = voteEvent.requireFirstTag(PubKeyTag.class);
    Optional<Relay> awardEventRelay = voteEvent.getRelayTag().map(RelayTag::getRelay);
    Relay awardEventConsolidatedRelay = awardEventRelay.orElse(fromRelay);
    EventTag eventTag = new EventTag(voteEvent.getId(), awardEventConsolidatedRelay.getUrl());

    Optional<CuratedBadgeAwardGenericEvent> curatedBadgeAwardGenericEvent = cacheCuratedBadgeAwardGenericEventServiceIF.getBy(recipientPublicKeyAsPubKeyTag, eventTag);
    if (curatedBadgeAwardGenericEvent.isPresent())
      return curatedBadgeAwardGenericEvent.map(EventIF::asGenericEventRecord);

    Optional<RelayTag> relayTag = voteEvent.findFirstTag(RelayTag.class);
    AddressTag suppliedAddressTag = voteEvent.requireFirstTag(AddressTag.class);

    Optional<CuratedBadgeDefinitionGenericEvent> curatedBadgeDefinitionGenericEvent =
       cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(suppliedAddressTag, relayTag, fromRelay);

    if (curatedBadgeDefinitionGenericEvent.isEmpty()) {
      log.debug("non-existent curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent).  return Optional.empty()");
      return Optional.empty();
    }

    log.debug("found existing curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent):\n{}\nre-composing BadgeAwardGenericEvent...",
       curatedBadgeDefinitionGenericEvent.get().createPrettyPrintJson());

    CuratedFormulaEvent formulaEvent =
       cacheCuratedFormulaEventServiceIF.getByAuthorAndIdentifierTag(
             curatedBadgeDefinitionGenericEvent.get().getPublicKey(),
             curatedBadgeDefinitionGenericEvent.get().getIdentifierTag())
          .orElseThrow(() -> new NostrException(
             String.format("no formulaEvent matches badgeDefinitionGenericEvent.asAddressableEventAddressTag():\n  %s",
                curatedBadgeDefinitionGenericEvent.get().getAddressTag().toStringPrettyPrint())));
    log.debug("(3of13V) Optional<FormulaEvent> formulaEvent:\n  {}", formulaEvent.createPrettyPrintJson());

    AddressTag formulaEventAddressableEventAddressTag = formulaEvent.asAddressableEventAddressTag();
    log.debug("(4of13V) calling cacheBadgeDefinitionReputationEventService.getByDirectTag(addressTag):\n  {}", formulaEvent.createPrettyPrintJson());
    BadgeDefinitionReputationEvent existingDefnReputation =
       cacheBadgeDefinitionReputationEventServiceIF.getByDirect(formulaEventAddressableEventAddressTag).stream().findFirst().orElseThrow(() ->
          new NostrException(String.format("no BadgeDefinitionReputationEvent found for formulaEventAddressableEventAddressTag:\n  %s",
             formulaEventAddressableEventAddressTag.toStringPrettyPrint())));

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       new BadgeAwardGenericEvent<>(
          voteEvent.asGenericEventRecord(), addressTag -> existingDefnReputation);

    CuratedBadgeAwardGenericEvent curatedBadgeAwardEvent = new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardGenericEvent,
       curatedBadgeDefinitionGenericEvent.get(),
       new ReferenceTag(
          badgeAwardGenericEvent.getRelayTag()
             .map(RelayTag::getRelay).map(Relay::getUrl)
             .orElse(fromRelay.getUrl())),
       superconductorRelay);

    super.processIncomingEvent(curatedBadgeAwardEvent, superconductorRelay);

    BadgeSetsEvent transientBadgeSetsEvent =
       cacheBadgeSetsEventServiceIF.getBy(
             new PubKeyTag(curatedBadgeAwardEvent.getAwardRecipientPublicKey()),
             existingDefnReputation.asAddressableEventAddressTag())
          .map(badgeSetsEvent1 ->
             badgeSetsEvent1.createNewFromExisting(
                superconductorInstanceIdentity, curatedBadgeAwardEvent))
          .orElse(
             new BadgeSetsEvent(
                superconductorInstanceIdentity,
                existingDefnReputation,
                curatedBadgeAwardEvent,
                superconductorRelay));

//    below saves but never deletes
    cacheServiceIF.save(transientBadgeSetsEvent);

    BadgeSetsEvent badgeSetsEvent = transientBadgeSetsEvent;
//       cacheBadgeSetsEventServiceIF.materialize(
//          badgeSetsEventKindPlugin.processIncomingEvent(transientBadgeSetsEvent, superconductorRelay)
//             .orElseThrow()).orElseThrow();

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(superconductorInstanceIdentity, badgeSetsEvent, superconductorRelay);
    return followSetsEventKindPlugin.processIncomingEvent(followSetsEvent, awardEventConsolidatedRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
