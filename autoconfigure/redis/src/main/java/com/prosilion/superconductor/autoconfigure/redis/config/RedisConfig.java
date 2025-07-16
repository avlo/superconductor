package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
import com.prosilion.superconductor.lib.redis.service.EventDocumentService;
import com.prosilion.superconductor.lib.redis.service.RedisCache;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;

@AutoConfiguration
@EnableRedisDocumentRepositories(
    basePackageClasses = {
        com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository.class
        ,
        com.prosilion.superconductor.lib.redis.document.EventDocument.class
    })
@EntityScan(
    basePackageClasses = {
        com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository.class
        ,
        com.prosilion.superconductor.lib.redis.document.EventDocument.class
    })
@ComponentScan(
    basePackages = {
        "com.prosilion.superconductor.base.service.clientresponse",
        "com.prosilion.superconductor.base.service.request",
        "com.prosilion.superconductor.base.util"
//        , "com.prosilion.superconductor.lib.redis.service"
    })
@ConditionalOnClass(RedisCache.class)
public class RedisConfig {

  @Bean
//  @ConditionalOnMissingBean
  EventDocumentService eventDocumentService(EventDocumentRepository eventDocumentRepository) {
    return new EventDocumentService(eventDocumentRepository);
  }

  @Bean
  @ConditionalOnMissingBean
  CacheIF cacheIF(EventDocumentService eventDocumentService) {
    return new RedisCache(eventDocumentService);
  }

  @Bean
  EventPluginIF eventPlugin(@NonNull CacheIF cacheIF) {
    return new EventPlugin(cacheIF);
  }

  @Bean
  @ConditionalOnMissingBean
  DataLoader dataLoader(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("upvoteBadgeDefinitionEvent") BadgeDefinitionEvent upvoteBadgeDefinitionEvent,
      @NonNull @Qualifier("downvoteBadgeDefinitionEvent") BadgeDefinitionEvent downvoteBadgeDefinitionEvent) {
    return new DataLoader(eventPlugin, upvoteBadgeDefinitionEvent, downvoteBadgeDefinitionEvent);
  }
}
