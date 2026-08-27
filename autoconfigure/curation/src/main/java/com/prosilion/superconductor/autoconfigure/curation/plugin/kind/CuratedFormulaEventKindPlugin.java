package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CuratedFormulaEventKindPlugin extends NonPublishingEventKindPlugin {
  private final Identity superconductorInstanceIdentity;
  private final Relay superconductorRelay;

  public CuratedFormulaEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelay = new Relay(superconductorRelayUrl);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFormulaEventIntoCuratedEvent, @NonNull Relay fromRelay) {
    log.debug("inside processIncomingEvent(incomingFormulaEventIntoCuratedEvent, fromRelay):\n{} ...",
       incomingFormulaEventIntoCuratedEvent.createPrettyPrintJson());

    Optional<RelayTag> eventRelayTag = incomingFormulaEventIntoCuratedEvent.findFirstTag(RelayTag.class);
    log.debug("... using eventRelayTag url [{}]",
       eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse("NULL"));

    String guaranteedSourceRelayUrl = eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse(fromRelay.getUrl());
    CuratedFormulaEvent curatedFormulaEvent = new CuratedFormulaEvent(incomingFormulaEventIntoCuratedEvent.asGenericEventRecord());

    log.debug("calling super.processIncomingEvent(curatedFormulaEvent, superconductorRelay):\n{}",
       curatedFormulaEvent.createPrettyPrintJson());
    return super.processIncomingEvent(curatedFormulaEvent, new Relay(guaranteedSourceRelayUrl));
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_FORMULA_EVENT;
  }
}
