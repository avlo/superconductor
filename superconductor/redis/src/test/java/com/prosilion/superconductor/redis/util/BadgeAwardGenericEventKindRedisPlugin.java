package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardGenericEventKindRedisPlugin<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends PublishingEventKindPlugin<T> {

  public BadgeAwardGenericEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF<T> eventKindPluginIF) {
    super(notifierService, eventKindPluginIF);
  }

  @Override
  public void processIncomingEvent(@NotNull T event) {
    super.processIncomingEvent(event);
  }
}
