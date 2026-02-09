package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.MaterializedEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import com.prosilion.superconductor.redis.util.BadgeAwardGenericEventKindRedisPlugin;
import com.prosilion.superconductor.redis.util.BadgeAwardReputationEventKindTypeRedisPlugin;
import com.prosilion.superconductor.redis.util.BadgeDefinitionGenericEventKindRedisPlugin;
import com.prosilion.superconductor.redis.util.BadgeDefinitionReputationEventKindTypeRedisPlugin;
import com.prosilion.superconductor.redis.util.FollowSetsEventKindRedisPlugin;
import com.prosilion.superconductor.redis.util.FormulaEventKindPlugin;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class NostrWsRedisTestConfig {

  @Bean("badgeAwardGenericEventKindPlugin")
  EventKindPluginIF badgeAwardGenericEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService) {
    BadgeAwardGenericEventKindRedisPlugin<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardGenericEventKindRedisPlugin =
        new BadgeAwardGenericEventKindRedisPlugin<>(
            notifierService,
            new MaterializedEventKindPlugin(
                Kind.BADGE_AWARD_EVENT,
                eventPlugin,
                cacheBadgeAwardGenericEventService),
            cacheBadgeAwardGenericEventService);
    return badgeAwardGenericEventKindRedisPlugin;
  }

  @Bean
  EventKindPluginIF badgeDefinitionGenericEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) {
    BadgeDefinitionGenericEventKindRedisPlugin badgeDefinitionGenericEventKindRedisPlugin =
        new BadgeDefinitionGenericEventKindRedisPlugin(
            new MaterializedEventKindPlugin(
                Kind.BADGE_DEFINITION_EVENT,
                eventPlugin,
                cacheBadgeDefinitionGenericEventService),
            cacheBadgeDefinitionGenericEventService);
    return badgeDefinitionGenericEventKindRedisPlugin;
  }

  @Bean
  EventKindPluginIF formulaEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheFormulaEventService cacheFormulaEventService) {
    MaterializedEventKindPlugin materializedEventKindPlugin = new MaterializedEventKindPlugin(
        Kind.ARBITRARY_CUSTOM_APP_DATA,
        eventPlugin,
        cacheFormulaEventService);
    FormulaEventKindPlugin formulaEventKindPlugin = new FormulaEventKindPlugin(
        materializedEventKindPlugin,
//        TODO: finish below rxr
        cacheFormulaEventService);
    return formulaEventKindPlugin;
  }

  @Bean
  EventKindTypePluginIF badgeAwardReputationEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService) {
    return new BadgeAwardReputationEventKindTypeRedisPlugin(
        notifierService,
        new EventKindTypePlugin(
            BADGE_AWARD_REPUTATION_KIND_TYPE,
            eventPlugin,
            cacheBadgeAwardReputationEventService),
        cacheBadgeAwardReputationEventService);
  }

  @Bean
  EventKindTypePluginIF badgeDefinitionReputationEventKindTypePlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    return new BadgeDefinitionReputationEventKindTypeRedisPlugin(
        new EventKindTypePlugin(
            BADGE_DEFINITION_REPUTATION_KIND_TYPE,
            eventPlugin,
            cacheBadgeDefinitionReputationEventService),
        cacheBadgeDefinitionReputationEventService);
  }

  @Bean
  EventKindPluginIF followSetsEventKindRedisPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull NotifierService notifierService,
      @NonNull CacheFollowSetsEventService cacheFollowSetsEventService) {
    FollowSetsEventKindRedisPlugin followSetsEventKindRedisPlugin = new FollowSetsEventKindRedisPlugin(
        notifierService,
        new MaterializedEventKindPlugin(
            Kind.FOLLOW_SETS,
            eventPlugin,
            cacheFollowSetsEventService),
        cacheFollowSetsEventService);
    return followSetsEventKindRedisPlugin;
  }
}
