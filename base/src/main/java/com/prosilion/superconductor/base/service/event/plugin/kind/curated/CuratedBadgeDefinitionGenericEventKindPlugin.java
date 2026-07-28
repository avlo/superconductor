package com.prosilion.superconductor.base.service.event.plugin.kind.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class CuratedBadgeDefinitionGenericEventKindPlugin extends NonPublishingEventKindPlugin {
  private final Identity superconductorInstanceIdentity;

  public CuratedBadgeDefinitionGenericEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    Optional<RelayTag> eventRelayTag = event.findFirstTag(RelayTag.class);
    Relay finalRelay = eventRelayTag.map(RelayTag::relay).orElse(fromRelay);
    log.debug("processing incoming BadgeDefinitionGenericEvent using [{}] url [{}]",
       eventRelayTag.isPresent() ? "event RelayTag" : "fromRelay", finalRelay.getUrl());

    if (eventRelayTag.isPresent()) {
      Optional<GenericEventRecord> genericEventRecord = super.processIncomingEvent(event, eventRelayTag.map(RelayTag::getRelay).orElseThrow());
      return genericEventRecord;
    }

    BadgeDefinitionGenericEvent reconstructedBadgeDefinitionGenericEvent = new BadgeDefinitionGenericEvent(event.asGenericEventRecord());
    CuratedBadgeDefinitionGenericEvent curatedBadgeDefinitionGenericEvent = new CuratedBadgeDefinitionGenericEvent(
       superconductorInstanceIdentity,
       reconstructedBadgeDefinitionGenericEvent,
       new ReferenceTag(fromRelay.getUrl()),
       fromRelay);

    super.processIncomingEvent(event, fromRelay); // STEP
    Optional<GenericEventRecord> curatedGenericEventRecord = super.processIncomingEvent(curatedBadgeDefinitionGenericEvent, fromRelay);
    return curatedGenericEventRecord;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
