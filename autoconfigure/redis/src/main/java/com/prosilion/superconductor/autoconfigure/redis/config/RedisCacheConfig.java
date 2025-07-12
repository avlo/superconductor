package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.redis.event.RedisCache;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@AutoConfiguration
//@EnableJpaRepositories(basePackages = "prosilion.superconductor.lib.jpa.repository")
//@EntityScan(basePackages = "prosilion.superconductor.lib.jpa.entity")
//@ComponentScan(basePackages = "prosilion.superconductor.lib.jpa.plugin.tag")
@ConditionalOnClass(RedisCache.class)
public class RedisCacheConfig {

  @Bean
  @ConditionalOnMissingBean
  CacheIF cacheIF() {
    return new RedisCache();
  }

  @Bean
  EventPluginIF eventPlugin(@NonNull CacheIF cacheIF) {
    return new EventPlugin(cacheIF);
  }
}
