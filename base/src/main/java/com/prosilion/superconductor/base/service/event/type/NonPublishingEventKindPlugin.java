package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for NonPublishingEventKind hierarchy
public class NonPublishingEventKindPlugin implements EventKindPluginIF<Kind> {
  private final EventKindPluginIF<Kind> eventKindPlugin;

  public NonPublishingEventKindPlugin(@NonNull EventKindPluginIF<Kind> eventKindPlugin) {
    this.eventKindPlugin = eventKindPlugin;
  }

  @Override
  public void processIncomingEvent(GenericEventKindIF event) {
    eventKindPlugin.processIncomingEvent(event);
  }

  @Override
  public Kind getKind() {
    return eventKindPlugin.getKind();
  }
}
