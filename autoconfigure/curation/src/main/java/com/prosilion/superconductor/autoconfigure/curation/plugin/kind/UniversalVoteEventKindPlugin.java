package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class UniversalVoteEventKindPlugin extends NonPublishingEventKindPlugin {
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;
  private final FollowSetsEventKindPlugin followSetsEventKindPlugin;
  private final Identity superconductorInstanceIdentity;
  private final Relay superconductorRelay;
  private final CacheServiceIF cacheServiceIF;

  public UniversalVoteEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull FollowSetsEventKindPlugin followSetsEventKindPlugin,
     @NonNull EventPluginIF eventPluginIF,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF) {
    super(eventPluginIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
    this.followSetsEventKindPlugin = followSetsEventKindPlugin;
    this.superconductorRelay = new Relay(superconductorRelayUrl);
    this.cacheServiceIF = cacheServiceIF;
    log.debug("using superconductorRelayUrl: [{}]", superconductorRelayUrl);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingVoteEvent, @NonNull Relay fromRelay) {
    log.debug("inside processIncomingEvent(incomingVoteEvent)\n{}", incomingVoteEvent.createPrettyPrintJson());

    Optional<GenericEventRecord> existingVoteEvent = cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT,
       incomingVoteEvent.requireFirstTag(PubKeyTag.class),
       new EventTag(
          incomingVoteEvent.getId(),
          incomingVoteEvent.findFirstTag(RelayTag.class).map(RelayTag::getRelay).map(Relay::getUrl).orElse(fromRelay.getUrl()))).stream().findFirst();
    if (existingVoteEvent.isPresent()) {
      log.debug("returning existing matching voteEvent");
      return Optional.of(incomingVoteEvent.asGenericEventRecord());
    }

    Relay awardEventConsolidatedRelay = incomingVoteEvent.getRelayTag().map(RelayTag::getRelay).orElse(fromRelay);
    log.debug("awardEventConsolidatedRelay: [{}]", awardEventConsolidatedRelay);

    AddressTag suppliedAddressTag = incomingVoteEvent.requireFirstTag(AddressTag.class);
    log.debug("calling cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(suppliedAddressTag):\n  {}", suppliedAddressTag);

    CuratedBadgeDefinitionGenericEvent curatedBadgeDefinitionGenericEvent =
       cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(suppliedAddressTag).orElseThrow(() ->
          new NostrException("non-existent curatedBadgeDefinitionGenericEvent1"));

    BadgeAwardCanonicalEvent reconstructedVoteEvent =
       new BadgeAwardCanonicalEvent(
          incomingVoteEvent.asGenericEventRecord(),
          addressTag -> curatedBadgeDefinitionGenericEvent.getBadgeDefinitionGenericEvent());

    CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent =
       new CuratedBadgeAwardGenericEvent(
          superconductorInstanceIdentity,
          reconstructedVoteEvent,
          curatedBadgeDefinitionGenericEvent,
          new ReferenceTag(fromRelay.getUrl()),
          superconductorRelay);
    super.processIncomingEvent(curatedBadgeAwardGenericEvent, fromRelay);
    return followSetsEventKindPlugin.processIncomingCuratedBadgeAwardGenericEvent(curatedBadgeAwardGenericEvent, fromRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
