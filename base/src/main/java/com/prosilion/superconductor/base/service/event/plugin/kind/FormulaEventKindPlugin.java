package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.service.event.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class FormulaEventKindPlugin extends NonPublishingEventKindPlugin {
  private final CacheFormulaEventServiceIF cacheFormulaEventServiceIF;

  public FormulaEventKindPlugin(
     @NonNull CacheFormulaEventServiceIF cacheFormulaEventServiceIF,
     @NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
    this.cacheFormulaEventServiceIF = cacheFormulaEventServiceIF;
    log.debug("FormulaEventKindPlugin ctor");
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFormulaEvent, @NonNull Relay fromRelay) {
    log.debug("inside processIncomingEvent(incomingFormulaEvent, fromRelay):\n{}",
       incomingFormulaEvent.createPrettyPrintJson());

    Optional<RelayTag> eventRelayTag = incomingFormulaEvent.findFirstTag(RelayTag.class);
    log.debug("... using eventRelayTag url [{}]",
       eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse("NULL"));

    FormulaEvent formulaEvent = cacheFormulaEventServiceIF.materialize(incomingFormulaEvent).orElseThrow();
    return super.processIncomingEvent(formulaEvent, fromRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
