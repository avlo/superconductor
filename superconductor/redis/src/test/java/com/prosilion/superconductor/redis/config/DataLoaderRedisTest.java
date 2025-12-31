package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
public class DataLoaderRedisTest implements DataLoaderRedisTestIF {
  private final CacheServiceIF cacheService;
  private final BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent;
  private final BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent;

  public DataLoaderRedisTest(
      @NonNull CacheServiceIF cacheService,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent) {
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
