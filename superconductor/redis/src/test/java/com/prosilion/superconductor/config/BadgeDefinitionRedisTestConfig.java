package com.prosilion.superconductor.config;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.NonNull;

@Configuration
//@PropertySource("classpath:kind-class-map.properties")
@Slf4j
public class BadgeDefinitionRedisTestConfig {

  @Bean(name = "badgeDefinitionUpvoteEvent")
  BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) {
    return new BadgeDefinitionGenericEvent(superconductorInstanceIdentity, new IdentifierTag(BaseIntegrationTestFixtures.AWARD_UNIT_UPVOTE), new Relay(superconductorRelayUrl));
  }

  @Bean(name = "badgeDefinitionDownvoteEvent")
  BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) {
    return new BadgeDefinitionGenericEvent(superconductorInstanceIdentity, new IdentifierTag(BaseIntegrationTestFixtures.AWARD_UNIT_DOWNVOTE), new Relay(superconductorRelayUrl));
  }

//  @Bean
//  @ConditionalOnMissingBean
//  DataLoaderRedisTestIF dataLoaderRedis(
//      @NonNull CacheServiceIF cacheService,
//      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent,
//      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent) {
//    log.info("class {} dataLoaderRedis() method called with bean parameters:\n  - {}\n  - {}\n  - {}",
//        getClass().getSimpleName(),
//        cacheService,
//        badgeDefinitionUpvoteEvent,
//        badgeDefinitionDownvoteEvent);
//    return new DataLoaderRedisTest(cacheService, badgeDefinitionUpvoteEvent, badgeDefinitionDownvoteEvent);
//  }
}
