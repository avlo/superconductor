package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeDefinitionGenericEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.DeleteEventKindPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
public class EventKindPluginConfig {
  @Bean
  @ConditionalOnMissingBean
  public DeleteEventKindPlugin deleteEventKindPlugin(
      @NonNull CacheServiceIF cacheService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new DeleteEventKindPlugin(eventPlugin, cacheService);
  }

  //  TODO: flexible autoconfigure variant of below, consider loading iff boolean/true is sets in app<xyz>.properties file
  @Bean
  @ConditionalOnMissingBean
  public BadgeDefinitionGenericEventKindPlugin badgeDefinitionGenericEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) {
    return new BadgeDefinitionGenericEventKindPlugin(
        eventPlugin,
        cacheBadgeDefinitionGenericEventService);
  }
}
