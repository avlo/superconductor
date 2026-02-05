package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for NonPublishingEventKind hierarchy
public class NonPublishingEventKindPlugin<T extends BaseEvent> implements EventKindPluginIF<T> {
  private final EventKindPluginIF<T> eventKindPlugin;

  public NonPublishingEventKindPlugin(@NonNull EventKindPluginIF<T> eventKindPlugin) {
    this.eventKindPlugin = eventKindPlugin;
  }

  @Override
  public void processIncomingEvent(T event) {
    eventKindPlugin.processIncomingEvent(event);
  }

  @Override
  public Kind getKind() {
    return eventKindPlugin.getKind();
  }
}
