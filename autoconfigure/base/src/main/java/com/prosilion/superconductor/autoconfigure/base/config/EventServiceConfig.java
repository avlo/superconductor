package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheBadgeSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventAuxService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventAuxService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.RemoteAbstractTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(
   basePackages = {
      "com.prosilion.superconductor.autoconfigure.base.config",
      "com.prosilion.superconductor.autoconfigure.base.service",
      "com.prosilion.superconductor.base.service.clientresponse",
      "com.prosilion.superconductor.base.service.request",
      "com.prosilion.superconductor.base.util",
   })
@Slf4j
public class EventServiceConfig {
  @Bean
  @ConditionalOnMissingBean
  CacheReferenceAddressTagService cacheDereferenceAddressTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteAbstractTagService remoteAbstractTagService) {
    return new CacheReferenceAddressTagService(cacheServiceIF, remoteAbstractTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheReferenceEventTagService cacheDereferenceEventTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteAbstractTagService remoteAbstractTagService) {
    return new CacheReferenceEventTagService(cacheServiceIF, remoteAbstractTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheKindAddressTagService cacheKindAddressTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteAbstractTagService remoteAbstractTagService) {
    return new CacheKindAddressTagService(cacheServiceIF, remoteAbstractTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheFormulaEventService cacheFormulaEventService(
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheDereferenceAddressTagService,
     @NonNull CacheKindAddressTagService cacheDereferenceKindAddressTagService) {
    return new CacheFormulaEventService(cacheDereferenceEventTagService, cacheDereferenceAddressTagService, cacheDereferenceKindAddressTagService);
  }

//  @Bean
//  @ConditionalOnMissingBean
//  CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService(
//     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
//     @NonNull CacheReferenceAddressTagService cacheDereferenceAddressTagService) {
//    return new CacheBadgeDefinitionGenericEventService(cacheDereferenceEventTagService, cacheDereferenceAddressTagService);
//  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeDefinitionGenericEventAuxService cacheBadgeDefinitionGenericEventAuxService(
     @NonNull CacheReferenceAddressTagService cacheDereferenceAddressTagService,
     @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) {
    return new CacheBadgeDefinitionGenericEventAuxService(cacheDereferenceAddressTagService, cacheBadgeDefinitionGenericEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService(
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheDereferenceAddressTagService,
     @NonNull CacheFormulaEventService cacheFormulaEventService,
     @NonNull CacheKindAddressTagService cacheKindAddressTagService) {
    return new CacheBadgeDefinitionReputationEventService(
       cacheDereferenceEventTagService,
       cacheDereferenceAddressTagService,
       cacheFormulaEventService,
       cacheKindAddressTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService(
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService,
     @NonNull CacheKindAddressTagService cacheKindAddressTagService) {
    return new CacheBadgeAwardGenericEventService(
       cacheDereferenceEventTagService,
       cacheBadgeDefinitionGenericEventService,
       cacheKindAddressTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeAwardGenericEventAuxService cacheBadgeAwardGenericEventAuxService(
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService) {
    return new CacheBadgeAwardGenericEventAuxService(
       cacheDereferenceEventTagService,
       cacheBadgeAwardGenericEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService(
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull CacheKindAddressTagService cacheDereferenceKindAddressTagService,
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    return new CacheBadgeAwardReputationEventService(
       cacheDereferenceEventTagService,
       cacheDereferenceKindAddressTagService,
       cacheBadgeDefinitionReputationEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeSetsEventService cacheBadgeSetsEventService(
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheReferenceAddressTagService,
     @NonNull CacheBadgeAwardGenericEventAuxService cacheBadgeAwardGenericEventAuxService,
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService,
     @NonNull CacheBadgeDefinitionGenericEventAuxService cacheBadgeDefinitionGenericEventAuxService) {
    return new CacheBadgeSetsEventService(cacheDereferenceEventTagService,
       cacheReferenceAddressTagService,
       cacheBadgeAwardGenericEventAuxService,
       cacheBadgeDefinitionReputationEventService,
       cacheBadgeDefinitionGenericEventAuxService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheFollowSetsEventService cacheFollowSetsEventService(
     @NonNull CacheReferenceEventTagService cacheDereferenceEventTagService,
     @NonNull @Qualifier("cacheBadgeAwardGenericEventAuxService") CacheBadgeAwardGenericEventAuxService cacheBadgeAwardGenericEventAuxService,
     @NonNull CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService,
     @NonNull CacheKindAddressTagService cacheDereferenceKindAddressTagService,
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF) {
    return new CacheFollowSetsEventService(
       cacheDereferenceEventTagService,
       cacheBadgeAwardGenericEventAuxService,
       cacheBadgeAwardReputationEventService,
       cacheDereferenceKindAddressTagService,
       cacheBadgeDefinitionReputationEventService,
       cacheBadgeSetsEventServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  EventPlugin eventPlugin(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventKindMaterializers") Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> eventKindMaterializers,
     @NonNull @Qualifier("eventKindTypeMaterializers") Map<Kind, Function<EventIF, Optional<? extends BaseEvent>>> eventKindTypeMaterializers,
     @NonNull @Qualifier("kindClassStringMap") Map<Kind, String> kindClassStringMap) {
    return new EventPlugin(
       cacheServiceIF,
       eventKindMaterializers,
       eventKindTypeMaterializers,
       kindClassStringMap);
  }
}
