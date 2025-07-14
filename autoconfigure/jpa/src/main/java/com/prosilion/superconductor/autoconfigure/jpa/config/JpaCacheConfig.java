package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.jpa.event.EventEntityService;
import com.prosilion.superconductor.lib.jpa.event.JpaCache;
import com.prosilion.superconductor.lib.jpa.service.DeletionEventEntityService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;

@AutoConfiguration
@EnableJpaRepositories(basePackages = "com.prosilion.superconductor.lib.jpa.repository")
@EntityScan(basePackages = "com.prosilion.superconductor.lib.jpa.entity")
@ComponentScan(basePackages = {
    "com.prosilion.superconductor.base.service.clientresponse",
    "com.prosilion.superconductor.base.service.request",
    "com.prosilion.superconductor.base.util",
    "com.prosilion.superconductor.lib.jpa.event",
    "com.prosilion.superconductor.lib.jpa.event.join.generic",
    "com.prosilion.superconductor.lib.jpa.plugin.tag",
    "com.prosilion.superconductor.lib.jpa.service"
})
@ConditionalOnClass(JpaCache.class)
public class JpaCacheConfig {

  @Bean
  @ConditionalOnMissingBean
  CacheIF cacheIF(
      @NonNull EventEntityService eventEntityService,
      @NonNull DeletionEventEntityService deletionEventEntityService) {
    return new JpaCache(eventEntityService, deletionEventEntityService);
  }

  @Bean
  EventPluginIF eventPlugin(@NonNull CacheIF cacheIF) {
    return new EventPlugin(cacheIF);
  }
}
