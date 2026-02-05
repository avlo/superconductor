package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.NonPublishingEventKindTypePlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionReputationEventKindTypeRedisPlugin extends NonPublishingEventKindTypePlugin<BadgeDefinitionReputationEvent> {
  CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventService;

  public BadgeDefinitionReputationEventKindTypeRedisPlugin(
      @NonNull EventKindTypePluginIF<BadgeDefinitionReputationEvent> eventKindTypePlugin,
      @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventService) {
    super(eventKindTypePlugin);
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public void processIncomingEvent(@NonNull BadgeDefinitionReputationEvent incomingBadgeDefinitionReputationEvent) {
    super.processIncomingEvent(cacheBadgeDefinitionReputationEventService.materialize(incomingBadgeDefinitionReputationEvent));
  }
}
