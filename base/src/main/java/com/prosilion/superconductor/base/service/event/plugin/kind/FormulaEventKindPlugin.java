package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormulaEventKindPlugin extends NonPublishingEventKindPlugin {
  public FormulaEventKindPlugin(@NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFormuleEvent, @NonNull Relay fromRelay) {
    log.debug("(passthrough->) calling super.processIncomingEvent(incomingFormuleEvent, fromRelay):\n{}",
       incomingFormuleEvent.createPrettyPrintJson());
    return super.processIncomingEvent(incomingFormuleEvent, fromRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.ARBITRARY_CUSTOM_APP_DATA;
  }
}
