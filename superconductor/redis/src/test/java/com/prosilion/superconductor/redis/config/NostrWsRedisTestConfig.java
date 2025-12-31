package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePlugin;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.EventKindPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.base.service.request.NotifierService;
import com.prosilion.superconductor.redis.util.BadgeAwardGenericEventKindRedisPlugin;
import com.prosilion.superconductor.redis.util.BadgeAwardReputationEventKindTypeRedisPlugin;
import com.prosilion.superconductor.redis.util.BadgeDefinitionGenericEventKindRedisPlugin;
import com.prosilion.superconductor.redis.util.BadgeDefinitionReputationEventKindTypeRedisPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_AWARD_REPUTATION;
import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_DEFINITION_REPUTATION;

//@Lazy
@Configuration
@ConditionalOnProperty(
    name = "server.ssl.enabled",
    havingValue = "false")
public class NostrWsRedisTestConfig {
  @Bean("badgeAwardGenericEventKindPlugin")
  EventKindPluginIF badgeAwardGenericEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new BadgeAwardGenericEventKindRedisPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.BADGE_AWARD_EVENT,
            eventPlugin));
  }

  @Bean
  EventKindTypePluginIF badgeAwardReputationEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new BadgeAwardReputationEventKindTypeRedisPlugin(
        notifierService,
        new EventKindTypePlugin(
            BADGE_AWARD_REPUTATION,
            eventPlugin));
  }

  @Bean
  EventKindPluginIF badgeDefinitionGenericEventKindPlugin(@NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new BadgeDefinitionGenericEventKindRedisPlugin(
        new EventKindPlugin(
            Kind.BADGE_DEFINITION_EVENT,
            eventPlugin));
  }

  @Bean
  EventKindTypePluginIF badgeDefinitionReputationEventKindTypePlugin(@NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new BadgeDefinitionReputationEventKindTypeRedisPlugin(
        new EventKindTypePlugin(
            BADGE_DEFINITION_REPUTATION,
            eventPlugin));
  }
}
