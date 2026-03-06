package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheDereferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheDereferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;

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
  CacheDereferenceAddressTagService cacheDereferenceAddressTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull String superconductorRelayUrl,
      @NonNull Duration requestTimeoutDuration) {
    return new CacheDereferenceAddressTagService(
        cacheServiceIF,
        superconductorRelayUrl,
        requestTimeoutDuration);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheDereferenceEventTagService cacheDereferenceEventTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull String superconductorRelayUrl,
      @NonNull Duration requestTimeoutDuration) {
    return new CacheDereferenceEventTagService(
        cacheServiceIF,
        superconductorRelayUrl,
        requestTimeoutDuration);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheFormulaEventService cacheFormulaEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagService cacheDereferenceEventTagService,
      @NonNull CacheDereferenceAddressTagService cacheDereferenceAddressTagService) {
    return new CacheFormulaEventService(cacheServiceIF, cacheDereferenceEventTagService, cacheDereferenceAddressTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService(
      @NonNull CacheDereferenceEventTagService cacheDereferenceEventTagService,
      @NonNull CacheDereferenceAddressTagService cacheDereferenceAddressTagService) {
    return new CacheBadgeDefinitionGenericEventService(cacheDereferenceEventTagService, cacheDereferenceAddressTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagService cacheDereferenceEventTagService,
      @NonNull CacheDereferenceAddressTagService cacheDereferenceAddressTagService,
      @NonNull CacheFormulaEventService cacheFormulaEventService) {
    return new CacheBadgeDefinitionReputationEventService(cacheServiceIF, cacheDereferenceEventTagService, cacheDereferenceAddressTagService, cacheFormulaEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService(
      @NonNull CacheDereferenceEventTagService cacheDereferenceEventTagService,
      @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) {
    return new CacheBadgeAwardGenericEventService(
        cacheDereferenceEventTagService,
        cacheBadgeDefinitionGenericEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService(
      @NonNull CacheDereferenceEventTagService cacheDereferenceEventTagService,
      @NonNull CacheDereferenceAddressTagService cacheDereferenceAddressTagService,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    return new CacheBadgeAwardReputationEventService(
        cacheDereferenceEventTagService,
        cacheDereferenceAddressTagService,
        cacheBadgeDefinitionReputationEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheFollowSetsEventService cacheFollowSetsEventService(
      @NonNull CacheDereferenceEventTagService cacheDereferenceEventTagService,
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService) {
    return new CacheFollowSetsEventService(
        cacheDereferenceEventTagService,
        cacheBadgeAwardGenericEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  EventPlugin eventPlugin(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull @Qualifier("eventKindMaterializers") Map<Kind, Function<EventIF, BaseEvent>> eventKindMaterializers,
      @NonNull @Qualifier("eventKindTypeMaterializers") Map<Kind, Function<EventIF, BaseEvent>> eventKindTypeMaterializers,
      @NonNull @Qualifier("kindClassStringMap") Map<String, String> kindClassStringMap) {
    return new EventPlugin(
        cacheServiceIF,
        eventKindMaterializers,
        eventKindTypeMaterializers,
        kindClassStringMap);
  }
}
