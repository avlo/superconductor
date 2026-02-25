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
  @ConditionalOnMissingBean(name = "kindClassStringMap")
  public Map<String, String> kindClassStringMap() {
    ResourceBundle relaysBundle = ResourceBundle.getBundle("kind-class-map");
    Map<String, String> collect = relaysBundle.keySet().stream()
        .collect(Collectors.toMap(key -> key, relaysBundle::getString));
    return collect;
  }
}

