package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class CuratedBadgeAwardGenericEventKindPlugin extends PublishingEventKindPlugin {
  private final Identity superconductorInstanceIdentity;
  private final Relay superconductorRelay;
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;

  public CuratedBadgeAwardGenericEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull NotifierService notifierService,
     @NonNull EventPluginIF eventPluginIF) {
    super(notifierService, eventPluginIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelay = new Relay(superconductorRelayUrl);
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    log.debug("processIncomingEvent(event, fromRelay) [{}]...\n{}", fromRelay.getUrl(), event.createPrettyPrintJson());

    Optional<RelayTag> relayTag = event.findFirstTag(RelayTag.class);
    AddressTag suppliedAddressTag = event.requireFirstTag(AddressTag.class);

    Optional<CuratedBadgeDefinitionGenericEvent> curatedBadgeDefinitionGenericEvent =
       cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(suppliedAddressTag);

    if (curatedBadgeDefinitionGenericEvent.isEmpty()) {
      log.debug("non-existent curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent).  return Optional.empty()");
      return Optional.empty();
    }

    log.debug("found existing curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent):\n{}\nre-composing BadgeAwardGenericEvent...",
       curatedBadgeDefinitionGenericEvent.get().createPrettyPrintJson());
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       new BadgeAwardGenericEvent<>(
          event.asGenericEventRecord(),
          aTag -> curatedBadgeDefinitionGenericEvent.get().getBadgeDefinitionGenericEvent());
    log.debug("...done:\n{}", badgeAwardGenericEvent.createPrettyPrintJson());

    log.debug("composing new CuratedBadgeAwardGenericEvent...");
    String guaranteedSourceRelayUrl = relayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse(fromRelay.getUrl());
    CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent = new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardGenericEvent,
       curatedBadgeDefinitionGenericEvent.get(),
       new ReferenceTag(guaranteedSourceRelayUrl),
       superconductorRelay);
    log.debug("...done:\n{}", curatedBadgeAwardGenericEvent.createPrettyPrintJson());

    log.debug("saving CuratedBadgeAwardGenericEvent with guaranteedSourceRelayUrl as ReferenceTag URL: [{}]", guaranteedSourceRelayUrl);
    return super.processIncomingEvent(curatedBadgeAwardGenericEvent, superconductorRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_AWARD_EVENT;
  }
}
