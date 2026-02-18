package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class FollowSetsEventKindRedisPlugin extends PublishingEventKindPlugin {
  CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;

  public FollowSetsEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF) {
    super(notifierService, eventPlugin);
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
  }

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF incomingFollowSetsEvent) {
    return super.processIncomingEvent(incomingFollowSetsEvent);
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
