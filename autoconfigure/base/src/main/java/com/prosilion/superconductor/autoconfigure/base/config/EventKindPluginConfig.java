package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.DeleteEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.StandardEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
public class EventKindPluginConfig {
  //  TODO: flexible autoconfigure variant of below, consider loading iff boolean/true is sets in app<xyz>.properties file

  @Bean("standardEventKindPlugins")
  @ConditionalOnMissingBean
  List<StandardEventKindPlugin> standardEventKindPlugins(
     @NonNull NotifierService notifierService,
     @NonNull EventPlugin eventPlugin,
     @NonNull @Qualifier("kindClassStringMap") Map<Kind, String> kindClassStringMap) {
    return kindClassStringMap.keySet().stream().map(kind ->
       new StandardEventKindPlugin(kind, notifierService, eventPlugin)).toList();
  }

  @Bean
  @ConditionalOnMissingBean
  public DeleteEventKindPlugin deleteEventKindPlugin(
     @NonNull CacheServiceIF cacheService,
     @NonNull EventPlugin eventPlugin) {
    return new DeleteEventKindPlugin(eventPlugin, cacheService);
  }
}
