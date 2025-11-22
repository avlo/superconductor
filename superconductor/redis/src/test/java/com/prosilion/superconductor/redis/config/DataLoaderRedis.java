package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DataLoaderRedis implements DataLoaderRedisIF {
  private final EventPluginIF eventPlugin;
  private final BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent;
  private final BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent;

  public DataLoaderRedis(
      @NonNull EventPluginIF eventPlugin,
      @NonNull BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent) {
    this.eventPlugin = eventPlugin;
    this.badgeDefinitionUpvoteEvent = badgeDefinitionUpvoteEvent;
    this.badgeDefinitionDownvoteEvent = badgeDefinitionDownvoteEvent;
  }

  @Override
  public void run(String... args) {
    log.info("{} processing incoming bean :\n  {}...", getClass().getSimpleName(), badgeDefinitionUpvoteEvent.getClass().getSimpleName());
    eventPlugin.processIncomingEvent(badgeDefinitionUpvoteEvent);
    log.info("...done");
    log.info("{} processing incoming bean :\n  {}...", getClass().getSimpleName(), badgeDefinitionDownvoteEvent.getClass().getSimpleName());
    eventPlugin.processIncomingEvent(badgeDefinitionDownvoteEvent);
    log.info("...done");
  }
}
