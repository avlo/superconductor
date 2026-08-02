package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.EventCurationActiveCondition;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheBadgeSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.CuratedBadgeAwardGenericEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.CuratedBadgeDefinitionGenericEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.CuratedFormulaEventKindPlugin;
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

@Slf4j
@AutoConfiguration
@Conditional(EventCurationActiveCondition.class)
public class EventCurationConfig {
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
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService,
     @NonNull CacheCuratedBadgeAwardGenericEventService cacheCurationSetsEventService) {
    return new CacheBadgeSetsEventService(
       cacheServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeDefinitionReputationEventService,
       cacheCurationSetsEventService);
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
     @NonNull NotifierService notifierService,
     @NonNull EventPlugin eventPlugin) {
    return new CuratedBadgeAwardGenericEventKindPlugin(
       superconductorInstanceIdentity, superconductorRelayUrl, notifierService, eventPlugin);
  }

  @Bean("formulaEventKindPlugin")
  @ConditionalOnMissingBean(name = "formulaEventKindPlugin")
  @Conditional(EventCurationActiveCondition.class)
  CuratedFormulaEventKindPlugin curatedFormulaEventKindPlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull EventPlugin eventPlugin) {
    return new CuratedFormulaEventKindPlugin(superconductorInstanceIdentity, superconductorRelayUrl, eventPlugin);
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
       cacheCuratedBadgeAwardGenericEventService::materialize);

    kindFxnMap.put(
       Kind.CURATION_SETS_BADGE_DEFINITION_EVENT,
       cacheCuratedBadgeDefinitionGenericEventService::materialize);

    kindFxnMap.put(
       Kind.CURATION_SETS_FORMULA_EVENT,
       cacheCuratedFormulaEventService::materialize);

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
}
