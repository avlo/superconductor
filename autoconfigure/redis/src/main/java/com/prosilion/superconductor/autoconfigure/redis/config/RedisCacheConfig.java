package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.redis.service.EventDocumentService;
import com.prosilion.superconductor.lib.redis.service.RedisCache;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;

@AutoConfiguration
@ComponentScan(basePackageClasses = com.prosilion.superconductor.lib.redis.service.EventDocumentService.class)
@ConditionalOnClass(RedisCache.class)
public class RedisCacheConfig {

  @Bean
  @ConditionalOnMissingBean
  CacheIF cacheIF(EventDocumentService eventDocumentService) {
    return new RedisCache(eventDocumentService);
  }

  @Bean
  EventPluginIF eventPlugin(@NonNull CacheIF cacheIF) {
    return new EventPlugin(cacheIF);
  }
}
