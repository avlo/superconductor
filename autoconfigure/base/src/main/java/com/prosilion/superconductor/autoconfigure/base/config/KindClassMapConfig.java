package com.prosilion.superconductor.autoconfigure.base.config;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
}

