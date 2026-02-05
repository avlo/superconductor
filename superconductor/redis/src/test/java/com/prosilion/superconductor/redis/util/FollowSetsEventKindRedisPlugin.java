package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class FollowSetsEventKindRedisPlugin extends PublishingEventKindPlugin<FollowSetsEvent> {
  CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;

  public FollowSetsEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF<FollowSetsEvent> eventKindPlugin,
      @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF) {
    super(notifierService, eventKindPlugin);
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
  }

  @Override
  public void processIncomingEvent(@NonNull FollowSetsEvent incomingFollowSetsEvent) {
    super.processIncomingEvent(cacheFollowSetsEventServiceIF.materialize(incomingFollowSetsEvent));
  }
}
