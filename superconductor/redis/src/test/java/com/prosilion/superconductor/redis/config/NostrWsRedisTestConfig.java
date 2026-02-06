package com.prosilion.superconductor.redis.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.DeleteEventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.CanonicalEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.DeleteEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPlugin;
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
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheTagMappedEventServiceIF<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventService) {
    BadgeAwardGenericEventKindRedisPlugin<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardGenericEventKindRedisPlugin =
        new BadgeAwardGenericEventKindRedisPlugin<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>>(
            notifierService,
            new EventKindPlugin(
                Kind.BADGE_AWARD_EVENT,
                eventPlugin,
                cacheBadgeAwardGenericEventService),
            (CacheBadgeAwardGenericEventServiceIF) cacheBadgeAwardGenericEventService);
    return badgeAwardGenericEventKindRedisPlugin;
  }

  @Bean
  EventKindPluginIF badgeDefinitionGenericEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheBadgeDefinitionGenericEventService") CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent> cacheBadgeDefinitionGenericEventService
//      @NonNull @Qualifier("cacheBadgeDefinitionGenericEventService") CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService
  ) {
    BadgeDefinitionGenericEventKindRedisPlugin badgeDefinitionGenericEventKindRedisPlugin =
        new BadgeDefinitionGenericEventKindRedisPlugin(
            new EventKindPlugin(
                Kind.BADGE_DEFINITION_EVENT,
                eventPlugin,
                cacheBadgeDefinitionGenericEventService),
            (CacheBadgeDefinitionGenericEventServiceIF) cacheBadgeDefinitionGenericEventService);
    return badgeDefinitionGenericEventKindRedisPlugin;
  }

  @Bean
  EventKindPluginIF formulaEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheFormulaEventService") CacheTagMappedEventServiceIF<FormulaEvent> cacheFormulaEventService) {
    EventKindPlugin eventKindPlugin = new EventKindPlugin(
        Kind.ARBITRARY_CUSTOM_APP_DATA,
        eventPlugin,
        cacheFormulaEventService);
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
      @NonNull @Qualifier("cacheBadgeAwardReputationEventService") CacheTagMappedEventServiceIF<BadgeAwardReputationEvent> cacheBadgeAwardReputationEventService) {
    return new BadgeAwardReputationEventKindTypeRedisPlugin(
        notifierService,
        new EventKindTypePlugin(
            BADGE_AWARD_REPUTATION_KIND_TYPE,
            eventPlugin,
            cacheBadgeAwardReputationEventService),
        (CacheBadgeAwardReputationEventServiceIF) cacheBadgeAwardReputationEventService);
  }

  @Bean
  EventKindTypePluginIF badgeDefinitionReputationEventKindTypePlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheBadgeDefinitionReputationEventService") CacheTagMappedEventServiceIF<BadgeDefinitionReputationEvent> cacheBadgeDefinitionReputationEventService) {
    return new BadgeDefinitionReputationEventKindTypeRedisPlugin(
        new EventKindTypePlugin(
            BADGE_DEFINITION_REPUTATION_KIND_TYPE,
            eventPlugin,
            cacheBadgeDefinitionReputationEventService),
        (CacheBadgeDefinitionReputationEventServiceIF) cacheBadgeDefinitionReputationEventService);
  }

  @Bean
  EventKindPluginIF followSetsEventKindRedisPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("cacheFollowSetsEventService") CacheTagMappedEventServiceIF<FollowSetsEvent> cacheFollowSetsEventService) {
    FollowSetsEventKindRedisPlugin followSetsEventKindRedisPlugin = new FollowSetsEventKindRedisPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.FOLLOW_SETS,
            eventPlugin,
            cacheFollowSetsEventService),
        (CacheFollowSetsEventServiceIF) cacheFollowSetsEventService);
    return followSetsEventKindRedisPlugin;
  }

  @Bean
//  @ConditionalOnMissingBean
  CanonicalEventKindPlugin textNoteEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheTagMappedEventServiceIF<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventService,
      @NonNull Map<String, String> kindClassStringMap) {
    log.debug("loaded canonical textNoteEventKindPlugin bean");
    return new CanonicalEventKindPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.TEXT_NOTE, eventPlugin, cacheBadgeAwardGenericEventService),
        kindClassStringMap);
  }

  @Bean
  @ConditionalOnMissingBean
  DeleteEventKindPlugin deleteEventKindPlugin(
      @NonNull CacheServiceIF cacheService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheTagMappedEventServiceIF<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventService) {
    return new DeleteEventKindPlugin(
        new EventKindPlugin(
            Kind.DELETION,
            eventPlugin, cacheBadgeAwardGenericEventService),
        new DeleteEventPlugin(cacheService));
  }
}
