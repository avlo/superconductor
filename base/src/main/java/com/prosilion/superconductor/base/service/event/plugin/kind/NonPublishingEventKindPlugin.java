package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for NonPublishingEventKind hierarchy
public abstract class NonPublishingEventKindPlugin implements EventKindPluginIF {
  private final EventPlugin eventPlugin;

  public NonPublishingEventKindPlugin(@NonNull EventPlugin eventPlugin) {
    this.eventPlugin = eventPlugin;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    eventPlugin.processIncomingEvent(event);
  }
}
