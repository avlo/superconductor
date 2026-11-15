package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.FormulaEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class CurationFormulaEventKindPlugin extends NonPublishingEventKindPlugin {
  private final FormulaEventKindPlugin formulaEventKindPlugin;
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final Boolean superconductorEventCurationActive;

  public CurationFormulaEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull FormulaEventKindPlugin formulaEventKindPlugin,
     @NonNull EventPluginIF eventPluginIF,
     @NonNull Boolean superconductorEventCurationActive) {
    super(eventPluginIF);
    this.formulaEventKindPlugin = formulaEventKindPlugin;
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.superconductorEventCurationActive = superconductorEventCurationActive;
    log.debug("FormulaEventKindPlugin ctor");
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFormulaEvent, @NonNull Relay fromRelay) {
    if (superconductorEventCurationActive) {
      incomingFormulaEvent = formulaEventKindPlugin.processIncomingEvent(incomingFormulaEvent, fromRelay).orElseThrow();
    }

    Optional<RelayTag> eventRelayTag = incomingFormulaEvent.findFirstTag(RelayTag.class);
    log.debug("... using eventRelayTag url [{}]",
       eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse("NULL"));

    String guaranteedSourceRelayUrl = eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse(fromRelay.getUrl());

    CuratedFormulaEvent curatedFormulaEvent = new CuratedFormulaEvent(
       superconductorInstanceIdentity,
       incomingFormulaEvent.asGenericEventRecord(),
       new ReferenceTag(guaranteedSourceRelayUrl),
       new Relay(superconductorRelayUrl));

    return super.processIncomingEvent(curatedFormulaEvent, fromRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
