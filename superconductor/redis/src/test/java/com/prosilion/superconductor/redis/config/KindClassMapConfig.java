package com.prosilion.superconductor.redis.config;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:kind-class-map.properties")
public class KindClassMapConfig {

  @Bean
  public @NonNull Map<String, String> kindClassStringMap() {
    ResourceBundle relaysBundle = ResourceBundle.getBundle("kind-class-map");
    Map<String, String> collect = relaysBundle.keySet().stream()
        .collect(Collectors.toMap(key -> key, relaysBundle::getString));
    return collect;
  }
}

