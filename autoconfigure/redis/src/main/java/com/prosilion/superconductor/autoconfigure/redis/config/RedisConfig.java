package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
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
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import com.prosilion.superconductor.base.controller.ApiUi;
import com.prosilion.superconductor.base.controller.EventApiUiIF;
import com.prosilion.superconductor.base.controller.ReqApiEventApiUi;
import com.prosilion.superconductor.base.controller.ReqApiUiIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntity;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntity;
import com.prosilion.superconductor.lib.redis.interceptor.RedisBaseTagIF;
import com.prosilion.superconductor.lib.redis.interceptor.TagInterceptor;
import com.prosilion.superconductor.lib.redis.repository.DeletionEventNosqlEntityRepository;
import com.prosilion.superconductor.lib.redis.repository.EventNosqlEntityByExampleRepository;
import com.prosilion.superconductor.lib.redis.repository.EventNosqlEntityRepository;
import com.prosilion.superconductor.lib.redis.service.DeletionEventNoSqlEntityService;
import com.prosilion.superconductor.lib.redis.service.EventNosqlEntityService;
import com.prosilion.superconductor.lib.redis.service.RedisCacheService;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.lang.NonNull;

@AutoConfiguration
@EnableRedisRepositories(
    basePackageClasses = {
        EventNosqlEntityRepository.class,
        DeletionEventNosqlEntityRepository.class,
        EventNosqlEntityByExampleRepository.class
    })
@EntityScan(
    basePackageClasses = {
        EventNosqlEntity.class,
        DeletionEventNosqlEntity.class
    })
@ComponentScan(
    basePackages = {
        "com.prosilion.superconductor.autoconfigure.base.service",
//        "com.prosilion.superconductor.base.service",
        "com.prosilion.superconductor.base.service.clientresponse",
        "com.prosilion.superconductor.base.service.request",
        "com.prosilion.superconductor.base.util",
        "com.prosilion.superconductor.lib.redis.repository",
        "com.prosilion.superconductor.lib.redis.interceptor",
        "com.prosilion.superconductor.lib.redis.service"
    })
@ConditionalOnClass(RedisCacheService.class)
public class RedisConfig {

  @Bean
  @ConditionalOnMissingBean
  EventNosqlEntityService eventNosqlEntityService(
      @NonNull EventNosqlEntityRepository eventNosqlEntityRepository,
      @NonNull EventNosqlEntityByExampleRepository eventNosqlEntityByExampleRepository,
      @NonNull List<TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors) {
    return new EventNosqlEntityService(eventNosqlEntityRepository, eventNosqlEntityByExampleRepository, interceptors);
  }

  @Bean
  @ConditionalOnMissingBean
  DeletionEventNoSqlEntityService deletionEventNoSqlEntityService(
      @NonNull DeletionEventNosqlEntityRepository deletionEventNosqlEntityRepository) {
    return new DeletionEventNoSqlEntityService(deletionEventNosqlEntityRepository);
  }

  @Bean(name = "cacheService")
  @ConditionalOnMissingBean
  RedisCacheService cacheService(
      @NonNull EventNosqlEntityService eventNosqlEntityService,
      @NonNull DeletionEventNoSqlEntityService deletionEventNoSqlEntityService) {
    return new RedisCacheService(eventNosqlEntityService, deletionEventNoSqlEntityService);
  }

  @Bean(name = "cacheDereferenceAddressTagService")
  CacheDereferenceAddressTagService cacheDereferenceAddressTagService(
      @NonNull CacheServiceIF cacheServiceIF) {
    return new CacheDereferenceAddressTagService(cacheServiceIF);
  }

  @Bean(name = "cacheDereferenceEventTagService")
  CacheDereferenceEventTagService cacheDereferenceEventTagService(
      @NonNull CacheServiceIF cacheServiceIF) {
    return new CacheDereferenceEventTagService(cacheServiceIF);
  }

  @Bean(name = "cacheFormulaEventService")
  CacheFormulaEventService cacheFormulaEventService(
      @NonNull RedisCacheService cacheService,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    return new CacheFormulaEventService(cacheService, cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagServiceIF);
  }

  @Bean(name = "cacheBadgeDefinitionGenericEventService")
  CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    return new CacheBadgeDefinitionGenericEventService(cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagServiceIF);
  }

  @Bean(name = "cacheBadgeDefinitionReputationEventService")
  CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService(
      @NonNull RedisCacheService cacheService,
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
  @ConditionalOnMissingBean
  EventPlugin eventPlugin(@NonNull RedisCacheService cacheService) {
    return new EventPlugin(cacheService);
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
