package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our CarDecorator for NonPublishingEventKind hierarchy
public abstract class NonPublishingEventKindPlugin implements EventKindPluginIF {
  private final EventPluginIF eventKindPluginIF;

  public NonPublishingEventKindPlugin(@NonNull EventPluginIF eventPluginIF) {
    this.eventKindPluginIF = eventPluginIF;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay relay) {
    return eventKindPluginIF.processIncomingEvent(event, relay);
  }
}
