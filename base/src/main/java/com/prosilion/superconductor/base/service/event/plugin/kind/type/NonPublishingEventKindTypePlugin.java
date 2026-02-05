package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for NonPublishingEventKindType hierarchy
public class NonPublishingEventKindTypePlugin<T extends BaseEvent> implements EventKindTypePluginIF<T> {
  private final EventKindTypePluginIF<T> eventKindTypePlugin;

  public NonPublishingEventKindTypePlugin(@NonNull EventKindTypePluginIF<T> eventKindTypePlugin) {
    this.eventKindTypePlugin = eventKindTypePlugin;
  }

  @Override
  public void processIncomingEvent(T event) {
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
