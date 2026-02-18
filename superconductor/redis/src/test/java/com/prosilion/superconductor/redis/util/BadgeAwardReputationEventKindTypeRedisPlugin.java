package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.PublishingEventKindTypePlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
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

  public GenericEventRecord processIncomingEvent(@NonNull EventIF incomingBadgeAwardReputationEvent) {
    return super.processIncomingEvent(cacheBadgeAwardReputationEventService.materialize(incomingBadgeAwardReputationEvent));
  }

  @Override
  public BadgeAwardReputationEvent materialize(EventIF eventIF) {
    return cacheBadgeAwardReputationEventService.materialize(eventIF);
  }
}
