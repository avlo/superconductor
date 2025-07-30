package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.jpa.service.JpaEventEntityService;
import com.prosilion.superconductor.lib.jpa.service.JpaCacheService;
import com.prosilion.superconductor.lib.jpa.service.JpaDeletionEventEntityService;
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
    "com.prosilion.superconductor.autoconfigure.jpa.config",
    "com.prosilion.superconductor.base.service.clientresponse",
    "com.prosilion.superconductor.base.service.request",
    "com.prosilion.superconductor.base.util",
    "com.prosilion.superconductor.lib.jpa.plugin.tag",
    "com.prosilion.superconductor.lib.jpa.service"
})
@ConditionalOnClass(JpaCacheService.class)
public class JpaCacheConfig {

  @Bean
  @ConditionalOnMissingBean
  CacheServiceIF cacheIF(
      @NonNull JpaEventEntityService jpaEventEntityService,
      @NonNull JpaDeletionEventEntityService jpaDeletionEventEntityService) {
    return new JpaCacheService(jpaEventEntityService, jpaDeletionEventEntityService);
  }

  @Bean
  EventPluginIF eventPlugin(@NonNull CacheServiceIF cacheServiceIF) {
    return new EventPlugin(cacheServiceIF);
  }
}
