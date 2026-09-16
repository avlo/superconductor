package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.RemoteEventQueryServiceIF;
import com.prosilion.superconductor.base.service.event.DeleteEventService;
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
  DeleteEventService deleteEventService(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF) {
    return new DeleteEventService(superconductorRelayUrl, superconductorInstanceIdentity, cacheServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheReferenceAddressTagService cacheReferenceAddressTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    return new CacheReferenceAddressTagService(cacheServiceIF, remoteEventQueryServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheReferenceEventTagService cacheReferenceEventTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    return new CacheReferenceEventTagService(cacheServiceIF, remoteEventQueryServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheKindAddressTagService cacheKindAddressTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    return new CacheKindAddressTagService(cacheServiceIF, remoteEventQueryServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheFormulaEventService cacheFormulaEventService(
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheReferenceAddressTagService,
     @NonNull CacheKindAddressTagService cacheKindAddressTagService) {
    return new CacheFormulaEventService(
       cacheReferenceEventTagService,
       cacheReferenceAddressTagService,
       cacheKindAddressTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheReferenceAddressTagService) {
    return new CacheBadgeDefinitionGenericEventService(
       cacheServiceIF,
       cacheReferenceEventTagService,
       cacheReferenceAddressTagService);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagService,
     @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService,
     @NonNull CacheKindAddressTagService cacheKindAddressTagService) {
    return new CacheBadgeAwardGenericEventService(
       cacheServiceIF,
       cacheReferenceEventTagService,
       cacheBadgeDefinitionGenericEventService,
       cacheKindAddressTagService);
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
