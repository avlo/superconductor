package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.KindClassMapService;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.redis.config.TestKindType.UNIT_DOWNVOTE;
import static com.prosilion.superconductor.redis.config.TestKindType.UNIT_UPVOTE;

@Configuration
@PropertySource("classpath:kind-class-map.properties")
public class BadgeDefinitionConfig {
  @Bean
  @ConditionalOnMissingBean
  DataLoaderRedisIF dataLoaderRedis(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent,
      @NonNull KindClassMapService kindClassMapService) throws ClassNotFoundException {
    ResourceBundle relaysBundle = ResourceBundle.getBundle("kind-class-map-test");
    Map<String, String> entrySet = relaysBundle.keySet().stream()
        .collect(Collectors.toMap(key -> key, relaysBundle::getString));
    for (Map.Entry<String, String> entry : entrySet.entrySet()) {
      kindClassMapService.add(Kind.valueOf(entry.getKey()), entry.getValue());
    }
    return new DataLoaderRedis(eventPlugin, badgeDefinitionUpvoteEvent, badgeDefinitionDownvoteEvent);
  }

  @Bean
  BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent(@NonNull Identity superconductorInstanceIdentity) {
    return new BadgeDefinitionAwardEvent(superconductorInstanceIdentity, new IdentifierTag(UNIT_UPVOTE.getName()));
  }

  @Bean
  BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent(@NonNull Identity superconductorInstanceIdentity) {
    return new BadgeDefinitionAwardEvent(superconductorInstanceIdentity, new IdentifierTag(UNIT_DOWNVOTE.getName()));
  }
}
