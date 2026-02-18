package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardGenericEventKindRedisPlugin<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends PublishingEventKindPlugin {
  private final CacheBadgeAwardGenericEventServiceIF<S, T> cacheBadgeAwardGenericEventServiceIF;

  public BadgeAwardGenericEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheBadgeAwardGenericEventServiceIF<S, T> cacheBadgeAwardGenericEventServiceIF) {
    super(notifierService, eventPlugin);
    this.cacheBadgeAwardGenericEventServiceIF = cacheBadgeAwardGenericEventServiceIF;
  }

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF event) {
    return super.processIncomingEvent(event);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
