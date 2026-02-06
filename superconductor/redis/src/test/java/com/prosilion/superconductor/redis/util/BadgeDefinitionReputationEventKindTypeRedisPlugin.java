package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.NonPublishingEventKindTypePlugin;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionReputationEventKindTypeRedisPlugin extends NonPublishingEventKindTypePlugin {
  CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventService;

  public BadgeDefinitionReputationEventKindTypeRedisPlugin(
      @NonNull EventKindTypePluginIF eventKindTypePlugin,
      @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventService) {
    super(eventKindTypePlugin);
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NotNull T event) {
    super.processIncomingEvent(cacheBadgeDefinitionReputationEventService.materialize(event));
  }

  @Override
  public BadgeDefinitionReputationEvent materialize(EventIF eventIF) {
    return cacheBadgeDefinitionReputationEventService.materialize(eventIF);
  }
}
