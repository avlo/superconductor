package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionGenericEventKindRedisPlugin extends NonPublishingEventKindPlugin {
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;

  public BadgeDefinitionGenericEventKindRedisPlugin(
      @NonNull EventKindPluginIF eventKindPluginIF,
      @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    super(eventKindPluginIF);
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    super.processIncomingEvent(event);
  }

  @Override
  public BadgeDefinitionGenericEvent materialize(EventIF eventIF) {
    BadgeDefinitionGenericEvent materialize = cacheBadgeDefinitionGenericEventServiceIF.materialize(eventIF);
    return materialize;
  }
}
