package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeDefinitionGenericEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class CurationBadgeDefinitionGenericEventKindPlugin extends NonPublishingEventKindPlugin {
  private final BadgeDefinitionGenericEventKindPlugin badgeDefinitionGenericEventKindPlugin;
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final Boolean superconductorEventCurationActive;

  public CurationBadgeDefinitionGenericEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull BadgeDefinitionGenericEventKindPlugin badgeDefinitionGenericEventKindPlugin,
     @NonNull EventPluginIF eventPluginIF,
     @NonNull Boolean superconductorEventCurationActive) {
    super(eventPluginIF);
    this.badgeDefinitionGenericEventKindPlugin = badgeDefinitionGenericEventKindPlugin;
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.superconductorEventCurationActive = superconductorEventCurationActive;
    log.debug("FormulaEventKindPlugin ctor");
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    if (superconductorEventCurationActive) {
      event = badgeDefinitionGenericEventKindPlugin.processIncomingEvent(event, fromRelay).orElseThrow();
    }

    Optional<RelayTag> eventRelayTag = event.findFirstTag(RelayTag.class);
    log.debug("... using eventRelayTag url [{}]",
       eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse("NULL"));

    String guaranteedSourceRelayUrl = eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse(fromRelay.getUrl());

    CuratedBadgeDefinitionGenericEvent curatedBadgeDefinitionGenericEvent = new CuratedBadgeDefinitionGenericEvent(
       superconductorInstanceIdentity,
       new BadgeDefinitionGenericEvent(event.asGenericEventRecord()),
       new ReferenceTag(guaranteedSourceRelayUrl),
       new Relay(superconductorRelayUrl));

    return super.processIncomingEvent(curatedBadgeDefinitionGenericEvent, fromRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
