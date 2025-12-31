package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.type.NonPublishingEventKindPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionGenericEventKindRedisPlugin extends NonPublishingEventKindPlugin {

  public BadgeDefinitionGenericEventKindRedisPlugin(@NonNull EventKindPluginIF eventKindPluginIF) {
    super(eventKindPluginIF);
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    super.processIncomingEvent(event);
  }
}
