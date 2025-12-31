package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.type.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardGenericEventKindRedisPlugin extends PublishingEventKindPlugin {

  public BadgeAwardGenericEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF eventKindPluginIF) {
    super(notifierService, eventKindPluginIF);
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    super.processIncomingEvent(event);
  }
}
