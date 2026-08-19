package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
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
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedFormulaEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.award.CacheCuratedBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheCuratedBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class UniversalVoteEventKindPlugin extends NonPublishingEventKindPlugin {
  private final CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService;
  private final CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService;
  private final CacheBadgeDefinitionReputationEventService badgeDefinitionReputationEventService;
  private final FollowSetsEventKindPlugin followSetsEventKindPlugin;
  private final CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF;
  private final BadgeSetsEventKindPlugin badgeSetsEventKindPlugin;
  private final Identity aImgIdentity;
  private final Relay relay;
  private final CacheServiceIF cacheServiceIF;

  public UniversalVoteEventKindPlugin(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull String afterimageRelayUrl,
     @NonNull CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService,
     @NonNull CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService,
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService,
     @NonNull FollowSetsEventKindPlugin followSetsEventKindPlugin,
     @NonNull CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF,
     @NonNull BadgeSetsEventKindPlugin badgeSetsEventKindPlugin,
     @NonNull EventPluginIF eventPluginIF,
     @NonNull Identity aImgIdentity) {
    super(eventPluginIF);
    this.cacheServiceIF = cacheServiceIF;
    this.aImgIdentity = aImgIdentity;
    this.cacheCuratedBadgeAwardGenericEventService = cacheCuratedBadgeAwardGenericEventService;
    this.cacheCuratedBadgeDefinitionGenericEventService = cacheCuratedBadgeDefinitionGenericEventService;
    this.badgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
    this.followSetsEventKindPlugin = followSetsEventKindPlugin;
    this.cacheCuratedFormulaEventServiceIF = cacheCuratedFormulaEventServiceIF;
    this.badgeSetsEventKindPlugin = badgeSetsEventKindPlugin;
    this.relay = new Relay(afterimageRelayUrl);
    log.debug("using afterimageRelayUrl: [{}]", afterimageRelayUrl);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF voteEvent, @NonNull Relay fromRelay) {
    log.debug("processing incoming voteEvent\n{}", voteEvent.createPrettyPrintJson());

    PubKeyTag recipientPublicKeyAsPubKeyTag = voteEvent.requireFirstTag(PubKeyTag.class);
    Optional<Relay> awardEventRelay = voteEvent.getRelayTag().map(RelayTag::getRelay);
    Relay awardEventConsolidatedRelay = awardEventRelay.orElse(fromRelay);
    EventTag eventTag = new EventTag(voteEvent.getId(), awardEventConsolidatedRelay.getUrl());

    Optional<CuratedBadgeAwardGenericEvent> curatedBadgeAwardGenericEvent = cacheCuratedBadgeAwardGenericEventService.getBy(recipientPublicKeyAsPubKeyTag, eventTag);
    if (curatedBadgeAwardGenericEvent.isPresent())
      return curatedBadgeAwardGenericEvent.map(EventIF::asGenericEventRecord);

    Optional<RelayTag> relayTag = voteEvent.findFirstTag(RelayTag.class);
    AddressTag suppliedAddressTag = voteEvent.requireFirstTag(AddressTag.class);

//    TODO: below in SC- pending investigation move below (and dependent functions) to CacheCuratedBadgeDefinitionGenericEventService    
    Optional<CuratedBadgeDefinitionGenericEvent> curatedBadgeDefinitionGenericEvent =
       cacheCuratedBadgeDefinitionGenericEventService.getByDirect(suppliedAddressTag, relayTag, fromRelay);

    if (curatedBadgeDefinitionGenericEvent.isEmpty()) {
      log.debug("non-existent curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent).  return Optional.empty()");
      return Optional.empty();
    }

    log.debug("found existing curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent):\n{}\nre-composing BadgeAwardGenericEvent...",
       curatedBadgeDefinitionGenericEvent.get().createPrettyPrintJson());

//    log.debug("(2of13V) saving incoming vote's CuratedBadgeDefinitionGenericEvent:\n  {}", curatedBadgeDefinitionEvent.createPrettyPrintJson());
//    TODO: below superfluous?  lines 85-86 does get() for the same event
//    super.processIncomingEvent(curatedBadgeDefinitionEvent, awardEventConsolidatedRelay);

    CuratedFormulaEvent formulaEvent =
       cacheCuratedFormulaEventServiceIF.getByAuthorAndIdentifierTag(
             curatedBadgeDefinitionGenericEvent.get().getPublicKey(),
             curatedBadgeDefinitionGenericEvent.get().getIdentifierTag())
          .orElseThrow(() -> new NostrException(
             String.format("no formulaEvent matches badgeDefinitionGenericEvent.asAddressableEventAddressTag():\n  %s",
                curatedBadgeDefinitionGenericEvent.get().getAddressTag().toStringPrettyPrint())));
    log.debug("(3of13V) Optional<FormulaEvent> formulaEvent:\n  {}", formulaEvent.createPrettyPrintJson());
//    TODO: below superfluous?  line 93 does a get() for the same event
//    super.processIncomingEvent(formulaEvent, relay);

    AddressTag formulaEventAddressableEventAddressTag = formulaEvent.asAddressableEventAddressTag();
    log.debug("(4of13V) calling cacheBadgeDefinitionReputationEventService.getByDirectTag(addressTag):\n  {}", formulaEvent.createPrettyPrintJson());
    BadgeDefinitionReputationEvent existingDefnReputation =
       badgeDefinitionReputationEventService.getByDirect(formulaEventAddressableEventAddressTag).stream().findFirst().orElseThrow(() ->
          new NostrException(String.format("no BadgeDefinitionReputationEvent found for formulaEventAddressableEventAddressTag:\n  %s",
             formulaEventAddressableEventAddressTag.toStringPrettyPrint())));

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       new BadgeAwardGenericEvent<>(
          voteEvent.asGenericEventRecord(), addressTag -> existingDefnReputation);

    CuratedBadgeAwardGenericEvent curatedBadgeAwardEvent = new CuratedBadgeAwardGenericEvent(
       aImgIdentity,
       badgeAwardGenericEvent,
       curatedBadgeDefinitionGenericEvent.get(),
       new ReferenceTag(
          badgeAwardGenericEvent.getRelayTag()
             .map(RelayTag::getRelay).map(Relay::getUrl)
             .orElse(fromRelay.getUrl())),
       relay);

    super.processIncomingEvent(curatedBadgeAwardEvent, relay);

    BadgeSetsEvent badgeSetsEvent = new BadgeSetsEvent(aImgIdentity, existingDefnReputation, curatedBadgeAwardEvent, relay);
    GenericEventRecord badgeSetsEventAsGenericEventRecord = badgeSetsEventKindPlugin.processIncomingEvent(badgeSetsEvent, relay).orElseThrow();

    BadgeSetsEvent dbSynchedBadgeSetsEvent = new BadgeSetsEvent(
       badgeSetsEventAsGenericEventRecord,
       badgeSetsEvent.getBadgeDefinitionReputationEvent(),
       badgeSetsEvent.getCuratedBadgeAwardGenericEventList());

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(aImgIdentity, dbSynchedBadgeSetsEvent, relay);
    followSetsEventKindPlugin.processIncomingEvent(followSetsEvent, awardEventConsolidatedRelay);

    log.debug("(13of13V) ... done.  returning upvoteEventReconstructed.asGenericEventRecord():\n  {}",
       Optional.of(curatedBadgeAwardEvent.asGenericEventRecord()).map(GenericEventRecord::createPrettyPrintJson).orElse("EMPTY OPTIONAL"));
    return Optional.of(curatedBadgeAwardEvent.asGenericEventRecord());
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
