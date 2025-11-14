package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeDefinitionAwardEventService;
import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
import com.prosilion.superconductor.base.controller.ApiUi;
import com.prosilion.superconductor.base.controller.EventApiUiIF;
import com.prosilion.superconductor.base.controller.ReqApiEventApiUi;
import com.prosilion.superconductor.base.controller.ReqApiUiIF;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginRxR;
import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntity;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntity;
import com.prosilion.superconductor.lib.redis.interceptor.RedisBaseTagIF;
import com.prosilion.superconductor.lib.redis.interceptor.TagInterceptor;
import com.prosilion.superconductor.lib.redis.repository.DeletionEventNosqlEntityRepository;
import com.prosilion.superconductor.lib.redis.repository.EventNosqlEntityRepository;
import com.prosilion.superconductor.lib.redis.service.DeletionEventNoSqlEntityService;
import com.prosilion.superconductor.lib.redis.service.EventNosqlEntityService;
import com.prosilion.superconductor.lib.redis.service.RedisCacheService;
import com.prosilion.superconductor.lib.redis.service.RedisCacheServiceIF;
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
        DeletionEventNosqlEntityRepository.class
    })
@EntityScan(
    basePackageClasses = {
        EventNosqlEntity.class,
        DeletionEventNosqlEntity.class
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
      @NonNull List<TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors) {
    return new EventNosqlEntityService(eventNosqlEntityRepository, interceptors);
  }

  @Bean
  @ConditionalOnMissingBean
  DeletionEventNoSqlEntityService deletionEventNoSqlEntityService(
      @NonNull DeletionEventNosqlEntityRepository deletionEventNosqlEntityRepository) {
    return new DeletionEventNoSqlEntityService(deletionEventNosqlEntityRepository);
  }

  @Bean
  @ConditionalOnMissingBean
  RedisCacheServiceIF redisCacheServiceIF(
      @NonNull EventNosqlEntityService eventNosqlEntityService,
      @NonNull DeletionEventNoSqlEntityService deletionEventNoSqlEntityService) {
    return new RedisCacheService(eventNosqlEntityService, deletionEventNoSqlEntityService);
  }

  @Bean(name = "cacheBadgeDefinitionAwardEventService")
  CacheEventTagBaseEventServiceIF cacheBadgeDefinitionAwardEventService(@NonNull RedisCacheServiceIF redisCacheServiceIF) {
    return new CacheBadgeDefinitionAwardEventService(redisCacheServiceIF);
  }

  @Bean
  CacheEventTagBaseEventServiceIF cacheFormulaEventService(
      @NonNull RedisCacheServiceIF redisCacheServiceIF,
      @NonNull @Qualifier("cacheBadgeDefinitionAwardEventService") CacheEventTagBaseEventServiceIF cacheBadgeDefinitionAwardEventService) {
    return new CacheFormulaEventService(redisCacheServiceIF, cacheBadgeDefinitionAwardEventService);
  }

//  @Bean(name = "eventPlugin")
//  EventPluginIF eventPlugin(@NonNull RedisCacheServiceIF redisCacheServiceIF) {
//    return new EventPlugin(redisCacheServiceIF);
//  }

  @Bean(name = "eventPlugin")
  EventPluginIF eventPlugin(
      @NonNull List<CacheEventTagBaseEventServiceIF> cacheEventTagBaseEventServiceIFS,
      @NonNull RedisCacheServiceIF redisCacheServiceIF) {
    return new EventPluginRxR(cacheEventTagBaseEventServiceIFS, redisCacheServiceIF);
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
