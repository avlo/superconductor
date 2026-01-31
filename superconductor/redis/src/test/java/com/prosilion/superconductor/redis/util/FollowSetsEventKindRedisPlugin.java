package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.type.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class FollowSetsEventKindRedisPlugin extends PublishingEventKindPlugin {
  CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;

  public FollowSetsEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF eventKindPlugin,
      @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF) {
    super(notifierService, eventKindPlugin);
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF incomingFollowSetsEvent) {
    super.processIncomingEvent(cacheFollowSetsEventServiceIF.materialize(incomingFollowSetsEvent.asGenericEventRecord()));
  }
}
