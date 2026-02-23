package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFollowSetsEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheDereferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheDereferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import com.prosilion.superconductor.base.controller.ApiUi;
import com.prosilion.superconductor.base.controller.EventApiUiIF;
import com.prosilion.superconductor.base.controller.ReqApiEventApiUi;
import com.prosilion.superconductor.base.controller.ReqApiUiIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.EventJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.deletion.DeletionEventJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.service.ConcreteTagEntitiesService;
import com.prosilion.superconductor.lib.jpa.service.DeletionEventJpaEntityService;
import com.prosilion.superconductor.lib.jpa.service.EventJpaEntityService;
import com.prosilion.superconductor.lib.jpa.service.GenericTagJpaEntitiesService;
import com.prosilion.superconductor.lib.jpa.service.JpaCacheService;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;

@AutoConfiguration
@EnableJpaRepositories(
    basePackages = {
        "com.prosilion.superconductor.lib.jpa.repository",
    })
@EntityScan(
    basePackages = {
        "com.prosilion.superconductor.lib.jpa.entity",
        "com.prosilion.superconductor.lib.jpa.entity.standard"
    })
@ComponentScan(
    basePackages = {
        "com.prosilion.superconductor.autoconfigure.base.service",
        "com.prosilion.superconductor.base.service.clientresponse",
        "com.prosilion.superconductor.base.service.request",
        "com.prosilion.superconductor.base.util",
        "com.prosilion.superconductor.lib.jpa.plugin.tag",
        "com.prosilion.superconductor.lib.jpa.repository",
        "com.prosilion.superconductor.lib.jpa.service"
    })
@ConditionalOnClass(JpaCacheService.class)
@Slf4j
public class JpaConfig {

  @Bean
  @ConditionalOnMissingBean
  EventJpaEntityService eventEntityService(
      @NonNull ConcreteTagEntitiesService<
          BaseTag,
          AbstractTagJpaEntityRepository<AbstractTagJpaEntity>,
          AbstractTagJpaEntity,
          EventEntityAbstractJpaEntity,
          EventEntityAbstractTagJpaEntityRepository<EventEntityAbstractJpaEntity>> concreteTagEntitiesService,
      @NonNull GenericTagJpaEntitiesService genericTagJpaEntitiesService,
      @NonNull EventJpaEntityRepository eventJpaEntityRepository) {
    return new EventJpaEntityService(concreteTagEntitiesService, genericTagJpaEntitiesService, eventJpaEntityRepository);
  }

  @Bean
  @ConditionalOnMissingBean
  DeletionEventJpaEntityService deletionEventEntityService(@NonNull DeletionEventJpaEntityRepository deletionEventJpaEntityRepository) {
    return new DeletionEventJpaEntityService(deletionEventJpaEntityRepository);
  }

  @Bean(name = "cacheService")
  @ConditionalOnMissingBean
  JpaCacheService cacheService(
      @NonNull EventJpaEntityService eventJpaEntityService,
      @NonNull DeletionEventJpaEntityService deletionEventJpaEntityService) {
    return new JpaCacheService(eventJpaEntityService, deletionEventJpaEntityService);
  }

  @Bean(name = "cacheDereferenceEventTagService")
  CacheDereferenceEventTagService cacheDereferenceEventTagService(
      @NonNull JpaCacheService cacheService,
      @NonNull String superconductorRelayUrl) {
    return new CacheDereferenceEventTagService(cacheService, superconductorRelayUrl);
  }

  @Bean(name = "cacheDereferenceAddressTagService")
  CacheDereferenceAddressTagService cacheDereferenceAddressTagService(
      @NonNull JpaCacheService cacheService,
      @NonNull String superconductorRelayUrl) {
    return new CacheDereferenceAddressTagService(cacheService, superconductorRelayUrl);
  }

  @Bean(name = "cacheFormulaEventService")
  CacheFormulaEventService cacheFormulaEventService(
      @NonNull JpaCacheService cacheService,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagService cacheDereferenceAddressTagService) {
    return new CacheFormulaEventService(cacheService, cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagService);
  }

  @Bean(name = "cacheBadgeDefinitionGenericEventService")
  CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    return new CacheBadgeDefinitionGenericEventService(cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagServiceIF);
  }

  @Bean(name = "cacheBadgeDefinitionReputationEventService")
  CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService(
      @NonNull JpaCacheService cacheService,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull @Qualifier("cacheFormulaEventService") CacheTagMappedEventServiceIF<FormulaEvent> cacheFormulaEventService) {
    return new CacheBadgeDefinitionReputationEventService(cacheService, cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagServiceIF, (CacheFormulaEventService) cacheFormulaEventService);
  }

  @Bean(name = "cacheBadgeAwardGenericEventService")
  CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull @Qualifier("cacheBadgeDefinitionGenericEventService") CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent> cacheBadgeDefinitionGenericEventService) {
    CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService = new CacheBadgeAwardGenericEventService(cacheDereferenceEventTagServiceIF, (CacheBadgeDefinitionGenericEventServiceIF) cacheBadgeDefinitionGenericEventService);
    return cacheBadgeAwardGenericEventService;
  }

  @Bean(name = "cacheBadgeAwardReputationEventService")
  CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull @Qualifier("cacheBadgeDefinitionReputationEventService") CacheTagMappedEventServiceIF<BadgeDefinitionReputationEvent> cacheBadgeDefinitionReputationEventService) {
    return new CacheBadgeAwardReputationEventService(
        cacheDereferenceEventTagServiceIF,
        cacheDereferenceAddressTagServiceIF,
        (CacheBadgeDefinitionReputationEventService) cacheBadgeDefinitionReputationEventService);
  }

  @Bean(name = "cacheFollowSetsEventService")
  CacheFollowSetsEventService cacheFollowSetsEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheTagMappedEventServiceIF<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventService) {
    return new CacheFollowSetsEventService(
        cacheDereferenceEventTagServiceIF,
        (CacheBadgeAwardGenericEventService) cacheBadgeAwardGenericEventService);
  }

  @Bean(name = "eventPlugin")
  EventPlugin eventPlugin(
      @NonNull JpaCacheService cacheService,
      @NonNull Map<Kind, Function<EventIF, BaseEvent>> eventKindMaterializers,
      @NonNull Map<Kind, Function<EventIF, BaseEvent>> eventKindTypeMaterializers,
      @NonNull Map<String, String> kindClassStringMap) {
    return new EventPlugin(
        cacheService,
        eventKindMaterializers,
        eventKindTypeMaterializers,
        kindClassStringMap);
  }

  @Bean
  @ConditionalOnMissingBean
  ReqApiEventApiUi reqApiEventApiUi(@NonNull EventApiUiIF eventApiUiIF, @NonNull ReqApiUiIF reqApiUiIF) {
    return new ApiUi(eventApiUiIF, reqApiUiIF);
  }

  @Bean
  @ConditionalOnMissingBean
  String superconductorRelayUrl(@NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl) {
    return superconductorRelayUrl;
  }

  @Bean
  @ConditionalOnMissingBean
  Identity superconductorInstanceIdentity(@NonNull @Value("${superconductor.key.private}") String privateKey) {
    return Identity.create(privateKey);
  }
}
