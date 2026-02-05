package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.PublishingEventKindTypePlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardReputationEventKindTypeRedisPlugin extends PublishingEventKindTypePlugin<BadgeAwardReputationEvent> {
  private final CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventService;

  public BadgeAwardReputationEventKindTypeRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindTypePluginIF<BadgeAwardReputationEvent> eventKindTypePlugin,
      @NonNull CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventService) {
    super(notifierService, eventKindTypePlugin);
    this.cacheBadgeAwardReputationEventService = cacheBadgeAwardReputationEventService;
  }

  @Override
  public void processIncomingEvent(@NonNull BadgeAwardReputationEvent incomingBadgeAwardReputationEvent) {
    super.processIncomingEvent(cacheBadgeAwardReputationEventService.materialize(incomingBadgeAwardReputationEvent));
  }
}
