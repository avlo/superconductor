package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class CuratedBadgeDefinitionGenericEventKindPlugin extends NonPublishingEventKindPlugin {
  private final Identity superconductorInstanceIdentity;
  private final Relay superconductorRelay;

  public CuratedBadgeDefinitionGenericEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelay = new Relay(superconductorRelayUrl);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    Optional<RelayTag> eventRelayTag = event.findFirstTag(RelayTag.class);
    log.debug("processing incoming BadgeDefinitionGenericEvent using eventRelayTag url [{}]",
       eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse("NULL"));
    
    super.processIncomingEvent(event, fromRelay);

    String guaranteedSourceRelayUrl = eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse(fromRelay.getUrl());
    CuratedBadgeDefinitionGenericEvent curatedBadgeDefinitionGenericEvent = new CuratedBadgeDefinitionGenericEvent(
       superconductorInstanceIdentity,
       new BadgeDefinitionGenericEvent(event.asGenericEventRecord()),
       new ReferenceTag(guaranteedSourceRelayUrl),
       superconductorRelay);

    log.debug("creating CuratedBadgeDefinitionGenericEvent referencing eventRelayTag url [{}]", guaranteedSourceRelayUrl);
    return super.processIncomingEvent(curatedBadgeDefinitionGenericEvent, superconductorRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
