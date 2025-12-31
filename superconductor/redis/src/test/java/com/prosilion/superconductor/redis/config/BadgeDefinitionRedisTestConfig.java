package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_DOWNVOTE;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;

@Configuration
//@PropertySource("classpath:kind-class-map.properties")
@Slf4j
public class BadgeDefinitionRedisTestConfig {

  @Bean(name = "badgeDefinitionUpvoteEvent")
  BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) {
    return new BadgeDefinitionAwardEvent(superconductorInstanceIdentity, new IdentifierTag(TEST_UNIT_UPVOTE), new Relay(superconductorRelayUrl));
  }

  @Bean(name = "badgeDefinitionDownvoteEvent")
  BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) {
    return new BadgeDefinitionAwardEvent(superconductorInstanceIdentity, new IdentifierTag(TEST_UNIT_DOWNVOTE), new Relay(superconductorRelayUrl));
  }

//  @Bean
//  @ConditionalOnMissingBean
//  DataLoaderRedisTestIF dataLoaderRedis(
//      @NonNull CacheServiceIF cacheService,
//      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
//      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent) {
//    log.info("class {} dataLoaderRedis() method called with bean parameters:\n  - {}\n  - {}\n  - {}",
//        getClass().getSimpleName(),
//        cacheService,
//        badgeDefinitionUpvoteEvent,
//        badgeDefinitionDownvoteEvent);
//    return new DataLoaderRedisTest(cacheService, badgeDefinitionUpvoteEvent, badgeDefinitionDownvoteEvent);
//  }
}
