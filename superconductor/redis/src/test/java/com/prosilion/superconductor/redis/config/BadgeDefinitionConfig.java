package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.util.TestKindType.UNIT_DOWNVOTE;
import static com.prosilion.superconductor.util.TestKindType.UNIT_UPVOTE;

@Configuration
@PropertySource("classpath:kind-class-map.properties")
@Slf4j
public class BadgeDefinitionConfig {
  @Bean(name = "badgeDefinitionUpvoteEvent")
  BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) {
    return new BadgeDefinitionAwardEvent(superconductorInstanceIdentity, new IdentifierTag(UNIT_UPVOTE.getName()), new Relay(superconductorRelayUrl));
  }

  @Bean(name = "badgeDefinitionDownvoteEvent")
  BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) {
    return new BadgeDefinitionAwardEvent(superconductorInstanceIdentity, new IdentifierTag(UNIT_DOWNVOTE.getName()), new Relay(superconductorRelayUrl));
  }

  @Bean
  @ConditionalOnMissingBean
  DataLoaderRedisIF dataLoaderRedis(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent) {
    log.info("class {} dataLoaderRedis() method called with bean parameters:\n  - {}\n  - {}\n  - {}",
        getClass().getSimpleName(),
        eventPlugin,
        badgeDefinitionUpvoteEvent,
        badgeDefinitionDownvoteEvent);
    return new DataLoaderRedis(eventPlugin, badgeDefinitionUpvoteEvent, badgeDefinitionDownvoteEvent);
  }
}
