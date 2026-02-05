package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
public class DataLoaderRedisTest implements DataLoaderRedisTestIF {
  private final CacheServiceIF cacheService;
  private final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent;
  private final BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent;

  public DataLoaderRedisTest(
      @NonNull CacheServiceIF cacheService,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent) {
    this.cacheService = cacheService;
    this.badgeDefinitionUpvoteEvent = badgeDefinitionUpvoteEvent;
    this.badgeDefinitionDownvoteEvent = badgeDefinitionDownvoteEvent;
  }

  @Override
  public void run(String... args) {
    log.info("{} processing incoming bean :\n  {}...", getClass().getSimpleName(), badgeDefinitionUpvoteEvent.getClass().getSimpleName());
    cacheService.save(badgeDefinitionUpvoteEvent);
    log.info("...done");
    log.info("{} processing incoming bean :\n  {}...", getClass().getSimpleName(), badgeDefinitionDownvoteEvent.getClass().getSimpleName());
    cacheService.save(badgeDefinitionDownvoteEvent);
    log.info("...done");
  }
}
