package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardGenericEventKindRedisPlugin<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends PublishingEventKindPlugin {
  private final CacheBadgeAwardGenericEventServiceIF<S, T> cacheBadgeAwardGenericEventServiceIF;

  public BadgeAwardGenericEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF eventKindPluginIF,
      @NonNull CacheBadgeAwardGenericEventServiceIF<S, T> cacheBadgeAwardGenericEventServiceIF) {
    super(notifierService, eventKindPluginIF);
    this.cacheBadgeAwardGenericEventServiceIF = cacheBadgeAwardGenericEventServiceIF;
  }

  @Override
  public <U extends BaseEvent> void processIncomingEvent(@NonNull U event) {
    super.processIncomingEvent(event);
  }

  @Override
  public T materialize(EventIF eventIF) {
    return cacheBadgeAwardGenericEventServiceIF.materialize(eventIF);
  }

//  @Override
//  public EventMaterializer<T> getEventMaterializer() {
//    return cacheBadgeAwardGenericEventServiceIF;
//  }
}
