package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.PublishingEventKindTypePlugin;
import com.prosilion.superconductor.base.service.request.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardReputationEventKindTypeRedisPlugin extends PublishingEventKindTypePlugin {
  private final CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventService;

  public BadgeAwardReputationEventKindTypeRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindTypePluginIF eventKindTypePlugin,
      @NonNull CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventService) {
    super(notifierService, eventKindTypePlugin);
    this.cacheBadgeAwardReputationEventService = cacheBadgeAwardReputationEventService;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF incomingBadgeAwardReputationEvent) {
    super.processIncomingEvent(cacheBadgeAwardReputationEventService.materialize(incomingBadgeAwardReputationEvent.asGenericEventRecord()));
  }
}
