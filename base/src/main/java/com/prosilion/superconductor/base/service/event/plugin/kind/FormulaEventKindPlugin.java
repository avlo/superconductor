package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class FormulaEventKindPlugin extends NonPublishingEventKindPlugin {
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final CacheFormulaEventServiceIF cacheFormulaEventServiceIF;

  public FormulaEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheFormulaEventServiceIF cacheFormulaEventServiceIF,
     @NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.cacheFormulaEventServiceIF = cacheFormulaEventServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFormulaEvent, @NonNull Relay fromRelay) {
    log.debug("inside processIncomingEvent(incomingFormulaEvent, fromRelay):\n{}",
       incomingFormulaEvent.createPrettyPrintJson());

    Optional<RelayTag> eventRelayTag = incomingFormulaEvent.findFirstTag(RelayTag.class);
    log.debug("... using eventRelayTag url [{}]",
       eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse("NULL"));

    Relay guaranteedSourceRelay = eventRelayTag.map(RelayTag::getRelay).orElse(fromRelay);

    if (cacheFormulaEventServiceIF.getEvent(incomingFormulaEvent.getId(), guaranteedSourceRelay).isPresent()) {
      log.info("return already existing identical incomingCuratedFormulaEvent");
      return Optional.of(incomingFormulaEvent.asGenericEventRecord());
    }

    Optional<FormulaEvent> formulaEventOpt = cacheFormulaEventServiceIF.materialize(incomingFormulaEvent);
    formulaEventOpt.ifPresent(formulaEvent -> super.processIncomingEvent(formulaEvent, fromRelay));

    CuratedFormulaEvent curatedFormulaEvent = new CuratedFormulaEvent(
       superconductorInstanceIdentity,
       incomingFormulaEvent.asGenericEventRecord(),
       new ReferenceTag(guaranteedSourceRelay.getUrl()),
       new Relay(superconductorRelayUrl));

    return super.processIncomingEvent(curatedFormulaEvent, fromRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
