package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.PublishingEventKindTypePlugin;
import com.prosilion.superconductor.base.service.request.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardEventKindTypeRedisPlugin extends PublishingEventKindTypePlugin {

  public BadgeAwardEventKindTypeRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindTypePluginIF<KindTypeIF> eventKindTypePlugin) {
    super(notifierService, eventKindTypePlugin);
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
    super.processIncomingEvent(event);
  }
}
