package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for NonPublishingEventKindType hierarchy
public abstract class NonPublishingEventKindTypePlugin implements EventKindTypePluginIF {
  private final EventKindTypePluginIF eventKindTypePlugin;

  public NonPublishingEventKindTypePlugin(@NonNull EventKindTypePluginIF eventKindTypePlugin) {
    this.eventKindTypePlugin = eventKindTypePlugin;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    eventKindTypePlugin.processIncomingEvent(event);
  }

  @Override
  public Kind getKind() {
    return eventKindTypePlugin.getKind();
  }

  @Override
  public KindTypeIF getKindType() {
    return eventKindTypePlugin.getKindType();
  }
}
