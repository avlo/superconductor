package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
import com.prosilion.superconductor.base.controller.ApiUi;
import com.prosilion.superconductor.base.controller.EventApiUiIF;
import com.prosilion.superconductor.base.controller.ReqApiEventApiUi;
import com.prosilion.superconductor.base.controller.ReqApiUiIF;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
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
        DeletionEventNosqlEntity.class,
        EventNosqlEntityByExampleRepository.class
    })
@ComponentScan(
    basePackages = {
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
  EventNosqlEntityService aventNosqlEntityService(
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

  @Bean
  @ConditionalOnMissingBean
  CacheServiceIF cacheServiceIF(
      @NonNull EventNosqlEntityService eventNosqlEntityService,
      @NonNull DeletionEventNoSqlEntityService deletionEventNoSqlEntityService) {
    return new RedisCacheService(eventNosqlEntityService, deletionEventNoSqlEntityService);
  }

  @Bean(name = "cacheFormulaEventService")
  CacheEventTagBaseEventServiceIF cacheFormulaEventService(
      @NonNull CacheServiceIF cacheServiceIF) {
    return new CacheFormulaEventService(cacheServiceIF);
  }

  @Bean(name = "cacheBadgeDefinitionReputationEventService")
  CacheEventTagBaseEventServiceIF cacheBadgeDefinitionReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheEventTagBaseEventServiceIF cacheFormulaEventService) {
    return new CacheBadgeDefinitionReputationEventService(cacheServiceIF, cacheFormulaEventService);
  }

  @Bean
  EventPluginIF eventPlugin(
      @NonNull List<CacheEventTagBaseEventServiceIF> cacheEventTagBaseEventServiceIFS,
      @NonNull CacheServiceIF cacheServiceIF) {
    return new EventPlugin(cacheEventTagBaseEventServiceIFS, cacheServiceIF);
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
