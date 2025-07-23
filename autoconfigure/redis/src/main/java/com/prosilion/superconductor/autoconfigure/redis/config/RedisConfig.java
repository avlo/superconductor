package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
import com.prosilion.superconductor.lib.redis.service.EventDocumentService;
import com.prosilion.superconductor.lib.redis.service.RedisCache;
import com.prosilion.superconductor.lib.redis.taginterceptor.InterceptorIF;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
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
        com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository.class
    })
@EntityScan(
    basePackageClasses = {
        com.prosilion.superconductor.lib.redis.document.EventDocument.class
    })
@ComponentScan(
    basePackages = {
        "com.prosilion.superconductor.base.service.clientresponse",
        "com.prosilion.superconductor.base.service.request",
        "com.prosilion.superconductor.base.util",
        "com.prosilion.superconductor.lib.redis.taginterceptor"
    })
@ConditionalOnClass(RedisCache.class)
public class RedisConfig {

  @Bean
//  @ConditionalOnMissingBean
  <T extends BaseTag> EventDocumentService<T> eventDocumentService(
      @NonNull EventDocumentRepository eventDocumentRepository,
      @NonNull List<InterceptorIF<T>> interceptors) {
    return new EventDocumentService<T>(eventDocumentRepository, interceptors);
  }

  @Bean
  @ConditionalOnMissingBean
  <T extends BaseTag> CacheIF cacheIF(EventDocumentService<T> eventDocumentService) {
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
