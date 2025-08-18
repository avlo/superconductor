package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for NonPublishingEventKindType hierarchy
public class NonPublishingEventKindTypePlugin implements EventKindTypePluginIF {
  private final EventKindTypePluginIF eventKindTypePlugin;

  public NonPublishingEventKindTypePlugin(@NonNull EventKindTypePluginIF eventKindTypePlugin) {
    this.eventKindTypePlugin = eventKindTypePlugin;
  }

  @Override
  public void processIncomingEvent(EventIF event) {
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
