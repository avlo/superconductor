package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeAwardGenericEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeDefinitionGenericEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.DeleteEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.FollowSetsEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.FormulaEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.BadgeAwardReputationEventKindTypePlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.BadgeDefinitionReputationEventKindTypePlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
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
  @Bean
  @ConditionalOnMissingBean
  BadgeAwardGenericEventKindPlugin<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardGenericEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPlugin eventPlugin) {
    return new BadgeAwardGenericEventKindPlugin<>(
        notifierService,
        eventPlugin);
  }

  @Bean
  @ConditionalOnMissingBean
  FormulaEventKindPlugin formulaEventKindPlugin(
      @NonNull EventPlugin eventPlugin) {
    return new FormulaEventKindPlugin(eventPlugin);
  }

  @Bean
  @ConditionalOnMissingBean
  BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPlugin eventPlugin) {
    return new BadgeAwardReputationEventKindTypePlugin(
        notifierService,
        new EventKindTypePlugin(
            BADGE_AWARD_REPUTATION_KIND_TYPE,
            eventPlugin));
  }

  @Bean
  @ConditionalOnMissingBean
  BadgeDefinitionReputationEventKindTypePlugin badgeDefinitionReputationEventKindTypePlugin(
      @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl,
      @NonNull EventPlugin eventPlugin) {
    return new BadgeDefinitionReputationEventKindTypePlugin(
        superconductorRelayUrl,
        new EventKindTypePlugin(
            BADGE_DEFINITION_REPUTATION_KIND_TYPE,
            eventPlugin));
  }

  @Bean
  @ConditionalOnMissingBean
  FollowSetsEventKindPlugin followSetsEventKindPlugin(
      @NonNull EventPlugin eventPlugin,
      @NonNull NotifierService notifierService) {
    return new FollowSetsEventKindPlugin(notifierService, eventPlugin);
  }

  @Bean
  @ConditionalOnMissingBean
  public DeleteEventKindPlugin deleteEventKindPlugin(
      @NonNull CacheServiceIF cacheService,
      @NonNull EventPlugin eventPlugin) {
    return new DeleteEventKindPlugin(eventPlugin, cacheService);
  }

  //  TODO: flexible autoconfigure variant of below, consider loading iff boolean/true is sets in app<xyz>.properties file
  @Bean
  @ConditionalOnMissingBean
  public BadgeDefinitionGenericEventKindPlugin badgeDefinitionGenericEventKindPlugin(
      @NonNull EventPlugin eventPlugin) {
    return new BadgeDefinitionGenericEventKindPlugin(eventPlugin);
  }

  @Bean
  @ConditionalOnMissingBean
  Map<Kind, Function<EventIF, BaseEvent>> eventKindMaterializers(
      @NonNull CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService,
      @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService,
      @NonNull CacheFollowSetsEventService cacheFollowSetsEventService,
      @NonNull CacheFormulaEventService cacheFormulaEventService) {
    Map<Kind, Function<EventIF, BaseEvent>> kindFxnMap = new HashMap<>();

    kindFxnMap.put(
        Kind.BADGE_AWARD_EVENT,
        cacheBadgeAwardGenericEventService::materialize);

    kindFxnMap.put(
        Kind.BADGE_DEFINITION_EVENT,
        cacheBadgeDefinitionGenericEventService::materialize);

    kindFxnMap.put(
        Kind.FOLLOW_SETS,
        cacheFollowSetsEventService::materialize);

    kindFxnMap.put(
        Kind.ARBITRARY_CUSTOM_APP_DATA,
        cacheFormulaEventService::materialize);

    kindFxnMap.put(
        Kind.DELETION,
        eventIF -> new DeletionEvent(
            eventIF.asGenericEventRecord()));

    return kindFxnMap;
  }

  @Bean
  @ConditionalOnMissingBean
  Map<Kind, Function<EventIF, BaseEvent>> eventKindTypeMaterializers(
      @NonNull CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    Map<Kind, Function<EventIF, BaseEvent>> kindFxnMap = new HashMap<>();

    kindFxnMap.put(
        Kind.BADGE_AWARD_EVENT,
        cacheBadgeAwardReputationEventService::materialize);

    kindFxnMap.put(
        Kind.BADGE_DEFINITION_EVENT,
        cacheBadgeDefinitionReputationEventService::materialize);

    return kindFxnMap;
  }
}
