package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for NonPublishingEventKind hierarchy
public abstract class NonPublishingEventKindPlugin implements EventKindPluginIF {
  private final EventPluginIF eventKindPluginIF;

  public NonPublishingEventKindPlugin(@NonNull EventPluginIF eventPluginIF) {
    this.eventKindPluginIF = eventPluginIF;
  }

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF event) {
    return eventKindPluginIF.processIncomingEvent(event);
  }
}
