package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for NonPublishingEventKind hierarchy
public abstract class NonPublishingEventKindPlugin implements EventKindPluginIF {
  private final EventKindPluginIF eventKindPlugin;

  public NonPublishingEventKindPlugin(@NonNull EventKindPluginIF eventKindPlugin) {
    this.eventKindPlugin = eventKindPlugin;
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    eventKindPlugin.processIncomingEvent(event);
  }

  @Override
  public Kind getKind() {
    return eventKindPlugin.getKind();
  }
}
