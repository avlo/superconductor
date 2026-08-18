package com.prosilion.superconductor.autoconfigure.curation.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.condition.EventCurationActiveCondition;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.autoconfigure.curation.calculator.DynamicReputationCalculator;
import com.prosilion.superconductor.autoconfigure.curation.calculator.ReputationCalculatorIF;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.BadgeSetsEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.CuratedBadgeAwardGenericEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.CuratedBadgeDefinitionGenericEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.CuratedFormulaEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.FollowSetsEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type.BadgeAwardReputationEventKindTypePlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type.BadgeDefinitionReputationEventKindTypePlugin;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.ReputationCalculationServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.award.CacheCuratedBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheCuratedBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.formula.CacheCuratedFormulaEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheBadgeSetsEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.reputation.ReputationCalculationLocalService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.event.plugin.kind.type.DeleteEventKindPlugin;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_KIND_TYPE;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_KIND_TYPE;

@Slf4j
@AutoConfiguration
@Conditional(EventCurationActiveCondition.class)
public class EventCurationActiveConfig {
  @Bean
  @ConditionalOnMissingBean
  ReputationCalculatorIF reputationCalculator(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) {
    return new DynamicReputationCalculator(
       superconductorRelayUrl,
       superconductorInstanceIdentity);
  }

  @Bean
  @ConditionalOnMissingBean
  ReputationCalculationServiceIF reputationCalculationService(
     @NonNull ReputationCalculatorIF reputationCalculator) {
    return new ReputationCalculationLocalService(reputationCalculator);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheDereferenceAddressTagService,
     @NonNull CacheCuratedFormulaEventService cacheCuratedFormulaEventService,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    return new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheDereferenceEventTagService,
       cacheDereferenceAddressTagService,
       cacheCuratedFormulaEventService,
       cacheKindAddressTagServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    return new CacheCuratedBadgeDefinitionGenericEventService(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       cacheServiceIF,
       cacheBadgeDefinitionGenericEventServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheCuratedFormulaEventService cacheCuratedFormulaEventService(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheFormulaEventServiceIF cacheFormulaEventServiceIF) {
    return new CacheCuratedFormulaEventService(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       cacheServiceIF,
       cacheFormulaEventServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeSetsEventService cacheBadgeSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF) {
    return new CacheBadgeSetsEventService(
       cacheServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheCuratedBadgeAwardGenericEventServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheFollowSetsEventService cacheFollowSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagService,
     @NonNull CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService,
     @NonNull CacheKindAddressTagService cacheDereferenceKindAddressTagService,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF) {
    return new CacheFollowSetsEventService(
       cacheServiceIF,
       cacheReferenceEventTagService,
       cacheBadgeAwardReputationEventService,
       cacheDereferenceKindAddressTagService,
       cacheBadgeSetsEventServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF) {
    return new CacheCuratedBadgeAwardGenericEventService(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       cacheServiceIF,
       cacheBadgeAwardGenericEventServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  @Conditional(EventCurationActiveCondition.class)
  CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull CacheKindAddressTagService cacheDereferenceKindAddressTagService,
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    return new CacheBadgeAwardReputationEventService(
       cacheServiceIF,
       cacheDereferenceEventTagService,
       cacheBadgeDefinitionReputationEventService,
       cacheDereferenceKindAddressTagService);
  }

  @ConditionalOnMissingBean
  @Bean("badgeDefinitionGenericEventKindPlugin")
  public CuratedBadgeDefinitionGenericEventKindPlugin curatedBadgeDefinitionGenericEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull EventPlugin eventPlugin) {
    return new CuratedBadgeDefinitionGenericEventKindPlugin(
       superconductorInstanceIdentity, superconductorRelayUrl, eventPlugin);
  }

  @Bean("badgeAwardGenericEventKindPlugin")
  @ConditionalOnMissingBean(name = "badgeAwardGenericEventKindPlugin")
  public CuratedBadgeAwardGenericEventKindPlugin curatedBadgeAwardGenericEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull NotifierService notifierService,
     @NonNull EventPlugin eventPlugin) {
    return new CuratedBadgeAwardGenericEventKindPlugin(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       cacheCuratedBadgeDefinitionGenericEventServiceIF,
       notifierService,
       eventPlugin);
  }

  @Bean("formulaEventKindPlugin")
  @ConditionalOnMissingBean(name = "formulaEventKindPlugin")
  CuratedFormulaEventKindPlugin curatedFormulaEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull EventPlugin eventPlugin) {
    return new CuratedFormulaEventKindPlugin(superconductorInstanceIdentity, superconductorRelayUrl, eventPlugin);
  }

  @Bean("badgeDefinitionReputationEventKindTypePlugin")
  @ConditionalOnMissingBean(name = "badgeDefinitionReputationEventKindTypePlugin")
  BadgeDefinitionReputationEventKindTypePlugin badgeDefinitionReputationEventKindTypePlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull EventPlugin eventPlugin) {
    return new BadgeDefinitionReputationEventKindTypePlugin(
       superconductorRelayUrl,
       new EventKindTypePlugin(
          BADGE_DEFINITION_REPUTATION_KIND_TYPE,
          eventPlugin));
  }

  @Bean("badgeSetsEventKindPlugin")
  @ConditionalOnMissingBean(name = "badgeSetsEventKindPlugin")
  BadgeSetsEventKindPlugin badgeSetsEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheBadgeSetsEventService cacheBadgeSetsEventService,
     @NonNull CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService,
     @NonNull DeleteEventKindPlugin deleteEventKindPlugin,
     @NonNull EventPlugin eventPlugin) {
    return new BadgeSetsEventKindPlugin(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       cacheBadgeSetsEventService,
       cacheCuratedBadgeAwardGenericEventService,
       deleteEventKindPlugin,
       eventPlugin);
  }

  @Bean("followSetsEventKindPlugin")
  @ConditionalOnMissingBean(name = "followSetsEventKindPlugin")
  FollowSetsEventKindPlugin followSetsEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull EventPlugin eventPlugin,
     @NonNull NotifierService notifierService,
     @NonNull DeleteEventKindPlugin deleteEventKindPlugin,
     @NonNull CacheFollowSetsEventService cacheFollowSetsEventService,
     @NonNull CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService,
     @NonNull BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin) {
    return new FollowSetsEventKindPlugin(
       superconductorRelayUrl,
       notifierService,
       eventPlugin,
       deleteEventKindPlugin,
       cacheFollowSetsEventService,
       cacheCuratedBadgeAwardGenericEventService,
       superconductorInstanceIdentity,
       badgeAwardReputationEventKindTypePlugin);
  }

  @Bean("badgeAwardReputationEventKindTypePlugin")
  @ConditionalOnMissingBean(name = "badgeAwardReputationEventKindTypePlugin")
  BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull EventPlugin eventPlugin,
     @NonNull NotifierService notifierService,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull ReputationCalculationServiceIF reputationCalculationServiceIF,
     @NonNull CacheFollowSetsEventService cacheFollowSetsEventService) {
    return new BadgeAwardReputationEventKindTypePlugin(
       superconductorRelayUrl,
       superconductorInstanceIdentity,
       notifierService,
       new EventKindTypePlugin(
          BADGE_AWARD_REPUTATION_KIND_TYPE,
          eventPlugin),
       cacheServiceIF,
       reputationCalculationServiceIF,
       cacheFollowSetsEventService);
  }

  @Bean("eventKindMaterializers")
  @ConditionalOnMissingBean(name = "eventKindMaterializers")
  Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> eventKindMaterializers(
     @NonNull CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService,
     @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService,
     @NonNull CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService,
     @NonNull CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService,
     @NonNull CacheCuratedFormulaEventService cacheCuratedFormulaEventService,
     @NonNull CacheBadgeSetsEventService cacheBadgeSetsEventService,
     @NonNull CacheFollowSetsEventService cacheFollowSetsEventService,
     @NonNull CacheFormulaEventService cacheFormulaEventService) {
    Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> kindFxnMap = new HashMap<>();

    kindFxnMap.put(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT,
       eventIF ->
          cacheCuratedBadgeAwardGenericEventService.materialize(eventIF));

    kindFxnMap.put(
       Kind.CURATION_SETS_BADGE_DEFINITION_EVENT,
       eventIF ->
          cacheCuratedBadgeDefinitionGenericEventService.materialize(eventIF));

    kindFxnMap.put(
       Kind.CURATION_SETS_FORMULA_EVENT,
       eventIF ->
          cacheCuratedFormulaEventService.materialize(eventIF));

    kindFxnMap.put(
       Kind.BADGE_AWARD_EVENT,
       eventIF ->
          cacheBadgeAwardGenericEventService.materialize(eventIF));

    kindFxnMap.put(
       Kind.BADGE_DEFINITION_EVENT,
       eventIF ->
          cacheBadgeDefinitionGenericEventService.materialize(eventIF));

    kindFxnMap.put(
       Kind.FOLLOW_SETS,
       eventIF ->
          cacheFollowSetsEventService.materialize(eventIF));

    kindFxnMap.put(
       Kind.BADGE_SETS_EVENT,
       eventIF ->
          cacheBadgeSetsEventService.materialize(eventIF));

    kindFxnMap.put(
       Kind.ARBITRARY_CUSTOM_APP_DATA,
       eventIF ->
          cacheFormulaEventService.materialize(eventIF));

    kindFxnMap.put(
       Kind.DELETION,
       eventIF -> Optional.of(new DeletionEvent(
          eventIF.asGenericEventRecord())));

    return kindFxnMap;
  }

  @Bean("eventKindTypeMaterializers")
  @ConditionalOnMissingBean(name = "eventKindTypeMaterializers")
  Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> eventKindTypeMaterializers(
     @NonNull CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService,
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> kindFxnMap = new HashMap<>();

    kindFxnMap.put(
       Kind.BADGE_AWARD_EVENT,
       eventIF ->
          cacheBadgeAwardReputationEventService.materialize(eventIF));

    kindFxnMap.put(
       Kind.BADGE_DEFINITION_EVENT,
       eventIF ->
          cacheBadgeDefinitionReputationEventService.materialize(eventIF));

    return kindFxnMap;
  }
}
