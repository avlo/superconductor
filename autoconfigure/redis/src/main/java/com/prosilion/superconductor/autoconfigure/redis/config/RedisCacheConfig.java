package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.redis.service.EventDocumentService;
import com.prosilion.superconductor.lib.redis.service.RedisCache;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;

@AutoConfiguration
@EnableRedisDocumentRepositories(basePackages = "com.prosilion.superconductor.lib.redis.repository")
@EntityScan(basePackages = "com.prosilion.superconductor.lib.redis.document")
@ComponentScan(basePackages = {
    "com.prosilion.superconductor.lib.redis.service",
    "com.prosilion.superconductor.base.service.request",
    "com.prosilion.superconductor.base.util",
    "com.prosilion.superconductor.base.service.clientresponse",
    "com.prosilion.superconductor.autoconfigure.redis.config"
})
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
