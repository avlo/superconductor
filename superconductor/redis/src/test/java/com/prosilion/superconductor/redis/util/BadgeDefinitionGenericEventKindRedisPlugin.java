package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionGenericEventKindRedisPlugin<T extends BadgeDefinitionGenericEvent> extends NonPublishingEventKindPlugin<T> {

  public BadgeDefinitionGenericEventKindRedisPlugin(@NonNull EventKindPluginIF<T> eventKindPluginIF) {
    super(eventKindPluginIF);
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    super.processIncomingEvent(event);
  }
}
