package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our CarDecorator for NonPublishingEventKindType hierarchy
public abstract class NonPublishingEventKindTypePlugin implements EventKindTypePluginIF {
  private final EventKindTypePluginIF eventKindTypePlugin;

  public NonPublishingEventKindTypePlugin(@NonNull EventKindTypePluginIF eventKindTypePlugin) {
    this.eventKindTypePlugin = eventKindTypePlugin;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay relay) {
    return eventKindTypePlugin.processIncomingEvent(event, relay);
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
