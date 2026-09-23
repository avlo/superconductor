package com.prosilion.superconductor.autoconfigure.curation.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.condition.EventCurationActiveCondition;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardCanonicalEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.autoconfigure.curation.calculator.ReputationCalculator;
import com.prosilion.superconductor.autoconfigure.curation.calculator.ReputationCalculatorIF;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.BadgeSetsEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.CuratedBadgeAwardCanonicalEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.CuratedBadgeDefinitionGenericEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.CuratedFormulaEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.FollowSetsEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.UniversalVoteEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type.BadgeAwardReputationEventKindTypePlugin;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type.BadgeDefinitionReputationEventKindTypePlugin;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardCanonicalEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedFormulaEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.ReputationCalculationServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.award.CacheCuratedBadgeAwardCanonicalEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheCuratedBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.formula.CacheCuratedFormulaEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheBadgeSetsEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.reputation.ReputationCalculationLocalService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeAwardCanonicalEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.service.event.DeleteEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeDefinitionGenericEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.FormulaEventKindPlugin;
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

  //  TODO: replace below w/ ReputationCalculatorConfig bean
  @Bean
  @ConditionalOnMissingBean
  ReputationCalculatorIF reputationCalculator(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) {
    return new ReputationCalculator(
       superconductorInstanceIdentity,
       superconductorRelayUrl);
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
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    return new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheDereferenceEventTagService,
       cacheDereferenceAddressTagService,
       cacheCuratedFormulaEventService,
       cacheCuratedBadgeDefinitionGenericEventServiceIF,
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
     @NonNull CacheFormulaEventServiceIF cacheFormulaEventServiceIF,
     @NonNull CacheReferenceAddressTagService cacheReferenceAddressTagService) {
    return new CacheCuratedFormulaEventService(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       cacheServiceIF,
       cacheFormulaEventServiceIF,
       cacheReferenceAddressTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeSetsEventService cacheBadgeSetsEventService(
     @NonNull Identity instanceIdentity,
     @NonNull String relayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheReferenceAddressTagService cacheReferenceAddressTagService,
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService,
     @NonNull CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService) {
    return new CacheBadgeSetsEventService(
       instanceIdentity,
       relayUrl,
       cacheServiceIF,
       cacheKindAddressTagServiceIF,
       cacheReferenceAddressTagService,
       cacheBadgeDefinitionReputationEventService,
       cacheCuratedBadgeAwardCanonicalEventService);
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
  CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeAwardCanonicalEventService cacheBadgeAwardCanonicalEventService,
     @NonNull CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService,
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagService) {
    return new CacheCuratedBadgeAwardCanonicalEventService(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       cacheServiceIF,
       cacheBadgeAwardCanonicalEventService,
       cacheCuratedBadgeDefinitionGenericEventService,
       cacheReferenceEventTagService);
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
  public BadgeDefinitionGenericEventKindPlugin badgeDefinitionGenericEventKindPlugin(
     @NonNull CuratedBadgeDefinitionGenericEventKindPlugin curatedBadgeDefinitionGenericEventKindPlugin) {
    return new BadgeDefinitionGenericEventKindPlugin(curatedBadgeDefinitionGenericEventKindPlugin);
  }

  @ConditionalOnMissingBean
  @Bean("curatedBadgeDefinitionGenericEventKindPlugin")
  public CuratedBadgeDefinitionGenericEventKindPlugin curatedBadgeDefinitionGenericEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService,
     @NonNull EventPlugin eventPlugin,
     @NonNull CacheServiceIF cacheServiceIF) {
    return new CuratedBadgeDefinitionGenericEventKindPlugin(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       cacheCuratedBadgeDefinitionGenericEventService,
       eventPlugin,
       cacheServiceIF);
  }

  @Bean("badgeAwardCanonicalEventKindPlugin")
  @ConditionalOnMissingBean(name = "badgeAwardCanonicalEventKindPlugin")
  UniversalVoteEventKindPlugin badgeAwardCanonicalEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService,
     @NonNull FollowSetsEventKindPlugin followSetsEventKindPlugin,
     @NonNull CuratedBadgeAwardCanonicalEventKindPlugin curatedBadgeAwardCanonicalEventKindPlugin,
     @NonNull Identity afterimageInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF) {
    return new UniversalVoteEventKindPlugin(
       superconductorRelayUrl,
       cacheCuratedBadgeDefinitionGenericEventService,
       followSetsEventKindPlugin,
       curatedBadgeAwardCanonicalEventKindPlugin,
       afterimageInstanceIdentity,
       cacheServiceIF);
  }

  @Bean("curatedBadgeAwardCanonicalEventKindPlugin")
  @ConditionalOnMissingBean(name = "curatedBadgeAwardCanonicalEventKindPlugin")
  public CuratedBadgeAwardCanonicalEventKindPlugin curatedBadgeAwardCanonicalEventKindPlugin(
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull NotifierService notifierService,
     @NonNull EventPlugin eventPlugin,
     CacheServiceIF cacheServiceIF) {
    return new CuratedBadgeAwardCanonicalEventKindPlugin(
       cacheCuratedBadgeDefinitionGenericEventServiceIF,
       notifierService,
       eventPlugin,
       cacheServiceIF);
  }

  @Bean("curatedFormulaEventKindPlugin")
  @ConditionalOnMissingBean(name = "curatedFormulaEventKindPlugin")
  CuratedFormulaEventKindPlugin curatedFormulaEventKindPlugin(
     @NonNull EventPlugin eventPlugin,
     @NonNull CacheServiceIF cacheServiceIF) {
    return new CuratedFormulaEventKindPlugin(
       eventPlugin,
       cacheServiceIF);
  }

  @Bean("formulaEventKindPlugin")
  @ConditionalOnMissingBean(name = "formulaEventKindPlugin")
  FormulaEventKindPlugin formulaEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CuratedFormulaEventKindPlugin curatedFormulaEventKindPlugin) {
    return new FormulaEventKindPlugin(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       curatedFormulaEventKindPlugin);
  }

  @Bean("badgeDefinitionReputationEventKindTypePlugin")
  @ConditionalOnMissingBean(name = "badgeDefinitionReputationEventKindTypePlugin")
  BadgeDefinitionReputationEventKindTypePlugin badgeDefinitionReputationEventKindTypePlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF,
     @NonNull EventPlugin eventPlugin,
     CacheServiceIF cacheServiceIF) {
    return new BadgeDefinitionReputationEventKindTypePlugin(
       superconductorRelayUrl,
       cacheCuratedFormulaEventServiceIF,
       new EventKindTypePlugin(
          BADGE_DEFINITION_REPUTATION_KIND_TYPE,
          eventPlugin),
       cacheServiceIF);
  }

  @Bean("badgeSetsEventKindPlugin")
  @ConditionalOnMissingBean(name = "badgeSetsEventKindPlugin")
  BadgeSetsEventKindPlugin badgeSetsEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheBadgeSetsEventService cacheBadgeSetsEventService,
     @NonNull CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService,
     @NonNull DeleteEventServiceIF deleteEventServiceIF,
     @NonNull EventPlugin eventPlugin,
     @NonNull CacheServiceIF cacheServiceIF) {
    return new BadgeSetsEventKindPlugin(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       cacheBadgeSetsEventService,
       cacheCuratedBadgeAwardCanonicalEventService,
       deleteEventServiceIF,
       eventPlugin,
       cacheServiceIF);
  }

  @Bean("followSetsEventKindPlugin")
  @ConditionalOnMissingBean(name = "followSetsEventKindPlugin")
  FollowSetsEventKindPlugin followSetsEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull EventPlugin eventPlugin,
     @NonNull NotifierService notifierService,
     @NonNull CacheFollowSetsEventService cacheFollowSetsEventService,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull BadgeSetsEventKindPlugin badgeSetsEventKindPlugin,
     @NonNull BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin,
     @NonNull CacheServiceIF cacheServiceIF) {
    return new FollowSetsEventKindPlugin(
       superconductorInstanceIdentity,
       superconductorRelayUrl,
       notifierService,
       eventPlugin,
       cacheFollowSetsEventService,
       cacheBadgeSetsEventServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       badgeSetsEventKindPlugin,
       badgeAwardReputationEventKindTypePlugin,
       cacheServiceIF);
  }

  @Bean("badgeAwardReputationEventKindTypePlugin")
  @ConditionalOnMissingBean(name = "badgeAwardReputationEventKindTypePlugin")
  BadgeAwardReputationEventKindTypePlugin badgeAwardReputationEventKindTypePlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull EventPlugin eventPlugin,
     @NonNull NotifierService notifierService,
     @NonNull ReputationCalculationServiceIF reputationCalculationServiceIF,
     @NonNull CacheFollowSetsEventService cacheFollowSetsEventService,
     @NonNull DeleteEventServiceIF deleteEventServiceIF,
     @NonNull CacheServiceIF cacheServiceIF) {
    return new BadgeAwardReputationEventKindTypePlugin(
       superconductorRelayUrl,
       superconductorInstanceIdentity,
       notifierService,
       new EventKindTypePlugin(
          BADGE_AWARD_REPUTATION_KIND_TYPE,
          eventPlugin),
       reputationCalculationServiceIF,
       cacheFollowSetsEventService,
       deleteEventServiceIF,
       cacheServiceIF);
  }

  @Bean("eventKindMaterializers")
  @ConditionalOnMissingBean(name = "eventKindMaterializers")
  Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> eventKindMaterializers(
     @NonNull CacheBadgeAwardCanonicalEventService cacheBadgeAwardCanonicalEventService,
     @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService,
     @NonNull CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService,
     @NonNull CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService,
     @NonNull CacheCuratedFormulaEventService cacheCuratedFormulaEventService,
     @NonNull CacheBadgeSetsEventService cacheBadgeSetsEventService,
     @NonNull CacheFollowSetsEventService cacheFollowSetsEventService,
     @NonNull CacheFormulaEventService cacheFormulaEventService) {
    Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> kindFxnMap = new HashMap<>();

    kindFxnMap.put(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT,
       cacheCuratedBadgeAwardCanonicalEventService::materialize);

    kindFxnMap.put(
       Kind.CURATION_SETS_BADGE_DEFINITION_EVENT,
       cacheCuratedBadgeDefinitionGenericEventService::materialize);

    kindFxnMap.put(
       Kind.CURATION_SETS_FORMULA_EVENT,
       cacheCuratedFormulaEventService::materialize);

    kindFxnMap.put(
       Kind.BADGE_AWARD_EVENT,
       cacheBadgeAwardCanonicalEventService::materialize);

    kindFxnMap.put(
       Kind.BADGE_DEFINITION_EVENT,
       cacheBadgeDefinitionGenericEventService::materialize);

    kindFxnMap.put(
       Kind.FOLLOW_SETS,
       cacheFollowSetsEventService::materialize);

    kindFxnMap.put(
       Kind.BADGE_SETS_EVENT,
       cacheBadgeSetsEventService::materialize);

    kindFxnMap.put(
       Kind.ARBITRARY_CUSTOM_APP_DATA,
       cacheFormulaEventService::materialize);

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
       cacheBadgeAwardReputationEventService::materialize);

    kindFxnMap.put(
       Kind.BADGE_DEFINITION_EVENT,
       cacheBadgeDefinitionReputationEventService::materialize);

    return kindFxnMap;
  }
}
