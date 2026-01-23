package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.NonPublishingEventKindTypePlugin;
import lombok.extern.slf4j.Slf4j;
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
  public void processIncomingEvent(@NonNull EventIF incomingBadgeDefinitionReputationEvent) {
    super.processIncomingEvent(cacheBadgeDefinitionReputationEventService.materialize(incomingBadgeDefinitionReputationEvent));
  }
}
