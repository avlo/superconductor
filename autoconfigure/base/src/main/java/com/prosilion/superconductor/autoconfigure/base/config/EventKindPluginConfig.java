package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeAwardGenericEventKindRedisPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeDefinitionGenericEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.DeleteEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.FollowSetsEventKindRedisPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.FormulaEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.BadgeAwardReputationEventKindTypeRedisPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.BadgeDefinitionReputationEventKindTypeRedisPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_KIND_TYPE;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_KIND_TYPE;

@Slf4j
@AutoConfiguration
public class EventKindPluginConfig {
  @Bean("badgeAwardGenericEventKindPlugin")
  EventKindPluginIF badgeAwardGenericEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin,
      @NonNull CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService) {
    BadgeAwardGenericEventKindRedisPlugin<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardGenericEventKindRedisPlugin =
        new BadgeAwardGenericEventKindRedisPlugin<>(
            notifierService,
            eventPlugin,
            cacheBadgeAwardGenericEventService::materialize);
    return badgeAwardGenericEventKindRedisPlugin;
  }

  @Bean
  EventKindPluginIF formulaEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin,
      @NonNull CacheFormulaEventService cacheFormulaEventService) {
    FormulaEventKindPlugin formulaEventKindPlugin = new FormulaEventKindPlugin(
        eventPlugin,
        cacheFormulaEventService::materialize);
    return formulaEventKindPlugin;
  }

  @Bean
  EventKindTypePluginIF badgeAwardReputationEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin,
      @NonNull CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService) {
    return new BadgeAwardReputationEventKindTypeRedisPlugin(
        notifierService,
        new EventKindTypePlugin(
            BADGE_AWARD_REPUTATION_KIND_TYPE,
            eventPlugin,
            cacheBadgeAwardReputationEventService::materialize));
  }

  @Bean
  EventKindTypePluginIF badgeDefinitionReputationEventKindTypePlugin(
      @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl,
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    return new BadgeDefinitionReputationEventKindTypeRedisPlugin(
        superconductorRelayUrl,
        new EventKindTypePlugin(
            BADGE_DEFINITION_REPUTATION_KIND_TYPE,
            eventPlugin,
            cacheBadgeDefinitionReputationEventService::materialize));
  }

  @Bean
  EventKindPluginIF followSetsEventKindRedisPlugin(
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin,
      @NonNull NotifierService notifierService,
      @NonNull CacheFollowSetsEventService cacheFollowSetsEventService) {
    return new FollowSetsEventKindRedisPlugin(
        notifierService,
        eventPlugin,
        cacheFollowSetsEventService::materialize);
  }

  @Bean
  @ConditionalOnMissingBean
  public DeleteEventKindPlugin deleteEventKindPlugin(
      @NonNull CacheServiceIF cacheService,
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin) {
    return new DeleteEventKindPlugin(eventPlugin, cacheService);
  }

  //  TODO: flexible autoconfigure variant of below, consider loading iff boolean/true is sets in app<xyz>.properties file
  @Bean
  @ConditionalOnMissingBean
  public BadgeDefinitionGenericEventKindPlugin badgeDefinitionGenericEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin,
      @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) {
    return new BadgeDefinitionGenericEventKindPlugin(
        eventPlugin,
        cacheBadgeDefinitionGenericEventService::materialize);
  }
}
