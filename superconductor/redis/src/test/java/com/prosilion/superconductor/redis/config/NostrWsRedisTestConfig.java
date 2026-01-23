package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.service.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.CacheTagMappedEventServiceIF;
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
import com.prosilion.superconductor.redis.util.FollowSetsEventKindRedisPlugin;
import com.prosilion.superconductor.redis.util.FormulaEventKindPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_AWARD_REPUTATION_KIND_TYPE;
import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_DEFINITION_REPUTATION_KIND_TYPE;

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
    BadgeAwardGenericEventKindRedisPlugin badgeAwardGenericEventKindRedisPlugin = new BadgeAwardGenericEventKindRedisPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.BADGE_AWARD_EVENT,
            eventPlugin));
    return badgeAwardGenericEventKindRedisPlugin;
  }

  @Bean
  EventKindPluginIF badgeDefinitionGenericEventKindPlugin(@NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    BadgeDefinitionGenericEventKindRedisPlugin badgeDefinitionGenericEventKindRedisPlugin = new BadgeDefinitionGenericEventKindRedisPlugin(
        new EventKindPlugin(
            Kind.BADGE_DEFINITION_EVENT,
            eventPlugin));
    return badgeDefinitionGenericEventKindRedisPlugin;
  }

  @Bean
  EventKindPluginIF formulaEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheFormulaEventService") CacheTagMappedEventServiceIF cacheFormulaEventService) {
    EventKindPlugin eventKindPlugin = new EventKindPlugin(
        Kind.ARBITRARY_CUSTOM_APP_DATA,
        eventPlugin);
    FormulaEventKindPlugin formulaEventKindPlugin = new FormulaEventKindPlugin(
        eventKindPlugin,
//        TODO: finish below rxr
        (CacheFormulaEventServiceIF) cacheFormulaEventService);
    return formulaEventKindPlugin;
  }

  @Bean
  EventKindTypePluginIF badgeAwardReputationEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheBadgeAwardReputationEventService") CacheTagMappedEventServiceIF cacheBadgeAwardReputationEventService) {
    return new BadgeAwardReputationEventKindTypeRedisPlugin(
        notifierService,
        new EventKindTypePlugin(
            BADGE_AWARD_REPUTATION_KIND_TYPE,
            eventPlugin),
        (CacheBadgeAwardReputationEventServiceIF) cacheBadgeAwardReputationEventService);
  }

  @Bean
  EventKindTypePluginIF badgeDefinitionReputationEventKindTypePlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheBadgeDefinitionReputationEventService") CacheTagMappedEventServiceIF cacheBadgeDefinitionReputationEventService) {
    return new BadgeDefinitionReputationEventKindTypeRedisPlugin(
        new EventKindTypePlugin(
            BADGE_DEFINITION_REPUTATION_KIND_TYPE,
            eventPlugin),
        (CacheBadgeDefinitionReputationEventServiceIF) cacheBadgeDefinitionReputationEventService);
  }

  @Bean
  EventKindPluginIF followSetsEventKindRedisPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("cacheFollowSetsEventService") CacheTagMappedEventServiceIF cacheFollowSetsEventService) {
    FollowSetsEventKindRedisPlugin followSetsEventKindRedisPlugin = new FollowSetsEventKindRedisPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.FOLLOW_SETS,
            eventPlugin),
        (CacheFollowSetsEventServiceIF) cacheFollowSetsEventService);
    return followSetsEventKindRedisPlugin;
  }
}
