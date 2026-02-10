package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.DeleteEventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.CanonicalEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.DeleteEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonMaterializedEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@PropertySource("classpath:kind-class-map.properties")
@Slf4j
public class KindClassMapConfig {

  @Bean("kindClassStringMap")
  @ConditionalOnMissingBean
  public Map<String, String> kindClassStringMap() {
    ResourceBundle relaysBundle = ResourceBundle.getBundle("kind-class-map");
    Map<String, String> collect = relaysBundle.keySet().stream()
        .collect(Collectors.toMap(key -> key, relaysBundle::getString));
//    log.debug("{} loading kindClassStringMap contents:\n{}", getClass().getSimpleName(), collect.entrySet());
    System.out.printf("{%s} loading kindClassStringMap contents:\n{%s}%n", getClass().getSimpleName(), collect.entrySet());
    return collect;
  }

//  @Bean
//  @ConditionalOnMissingBean
//  public ParameterizedEventKindPlugin parameterizedEventKindPlugin(
//      @NonNull NotifierService notifierService,
//      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
//      @NonNull CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService,
//      @NonNull @Qualifier("kindClassStringMap") Map<String, String> kindClassStringMap) {
//    ParameterizedEventKindPlugin canonicalEventKindPlugin = new ParameterizedEventKindPlugin(
//        notifierService,
//        new MaterializedEventKindPlugin(
//            Kind.TEXT_NOTE, eventPlugin, cacheBadgeAwardGenericEventService),
//        kindClassStringMap);
//    System.out.printf("[%s] loaded canonical textNoteEventKindPlugin bean", getClass().getSimpleName());
//    System.out.printf("with kindClassStringMap contents:\n{%s}%n", kindClassStringMap.entrySet());
//    return canonicalEventKindPlugin;
//  }

  @Bean("defaultEventKindPlugin")
  @ConditionalOnMissingBean
  public CanonicalEventKindPlugin defaultEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    CanonicalEventKindPlugin canonicalEventKindPlugin = new CanonicalEventKindPlugin(
        notifierService,
        new NonMaterializedEventKindPlugin(
            Kind.TEXT_NOTE, eventPlugin));
    System.out.printf("[%s] loaded canonical defaultEventKindPlugin bean", canonicalEventKindPlugin.getClass().getSimpleName());
    return canonicalEventKindPlugin;
  }

  @Bean("deleteEventKindPlugin")
  @ConditionalOnMissingBean
  public DeleteEventKindPlugin deleteEventKindPlugin(
      @NonNull CacheServiceIF cacheService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new DeleteEventKindPlugin(
        new NonMaterializedEventKindPlugin(
            Kind.DELETION, eventPlugin),
        new DeleteEventPlugin(cacheService));
  }
}

