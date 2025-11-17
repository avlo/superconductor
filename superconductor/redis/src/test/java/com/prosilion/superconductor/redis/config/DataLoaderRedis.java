package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

public class DataLoaderRedis implements DataLoaderRedisIF {
  private final EventPluginIF eventPlugin;
  private final BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent;
  private final BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent;

  public DataLoaderRedis(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent) {
    this.eventPlugin = eventPlugin;
    this.badgeDefinitionUpvoteEvent = badgeDefinitionUpvoteEvent;
    this.badgeDefinitionDownvoteEvent = badgeDefinitionDownvoteEvent;
  }

  @Override
  public void run(String... args) {
    eventPlugin.processIncomingEvent(badgeDefinitionUpvoteEvent);
    eventPlugin.processIncomingEvent(badgeDefinitionDownvoteEvent);
  }
}
