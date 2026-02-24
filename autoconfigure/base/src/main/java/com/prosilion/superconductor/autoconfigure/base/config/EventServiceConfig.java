package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheDereferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheDereferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
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
      @NonNull String superconductorRelayUrl) {
    return new CacheDereferenceAddressTagService(cacheServiceIF, superconductorRelayUrl);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheDereferenceEventTagService cacheDereferenceEventTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull String superconductorRelayUrl) {
    return new CacheDereferenceEventTagService(cacheServiceIF, superconductorRelayUrl);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheFormulaEventService cacheFormulaEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    return new CacheFormulaEventService(cacheServiceIF, cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    return new CacheBadgeDefinitionGenericEventService(cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull @Qualifier("cacheFormulaEventService") CacheTagMappedEventServiceIF<FormulaEvent> cacheFormulaEventService) {
    return new CacheBadgeDefinitionReputationEventService(cacheServiceIF, cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagServiceIF, (CacheFormulaEventService) cacheFormulaEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull @Qualifier("cacheBadgeDefinitionGenericEventService") CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent> cacheBadgeDefinitionGenericEventService) {
    return new CacheBadgeAwardGenericEventService(
        cacheDereferenceEventTagServiceIF,
        (CacheBadgeDefinitionGenericEventServiceIF) cacheBadgeDefinitionGenericEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull @Qualifier("cacheBadgeDefinitionReputationEventService") CacheTagMappedEventServiceIF<BadgeDefinitionReputationEvent> cacheBadgeDefinitionReputationEventService) {
    return new CacheBadgeAwardReputationEventService(
        cacheDereferenceEventTagServiceIF,
        cacheDereferenceAddressTagServiceIF,
        (CacheBadgeDefinitionReputationEventService) cacheBadgeDefinitionReputationEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheFollowSetsEventService cacheFollowSetsEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheTagMappedEventServiceIF<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventService) {
    return new CacheFollowSetsEventService(
        cacheDereferenceEventTagServiceIF,
        (CacheBadgeAwardGenericEventService) cacheBadgeAwardGenericEventService);
  }

  @Bean
  @ConditionalOnMissingBean
  EventPlugin eventPlugin(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull @Qualifier("eventKindMaterializers") Map<Kind, Function<EventIF, BaseEvent>> eventKindMaterializers,
      @NonNull @Qualifier("eventKindTypeMaterializers") Map<Kind, Function<EventIF, BaseEvent>> eventKindTypeMaterializers,
      @NonNull Map<String, String> kindClassStringMap) {
    return new EventPlugin(
        cacheServiceIF,
        eventKindMaterializers,
        eventKindTypeMaterializers,
        kindClassStringMap);
  }
}

