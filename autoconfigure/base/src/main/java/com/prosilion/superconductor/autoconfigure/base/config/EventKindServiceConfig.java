package com.prosilion.superconductor.autoconfigure.base.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.base.service.event.EventService;
import com.prosilion.superconductor.base.service.event.kind.EventKindService;
import com.prosilion.superconductor.base.service.event.kind.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeService;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
public class EventKindServiceConfig {
  @Bean(name = "eventKindService")
  @ConditionalOnMissingBean
  EventKindService eventKindService(
      @NonNull List<EventKindPluginIF> eventKindPlugins,
      @NonNull Map<String, String> kindClassStringMap) {
    return new EventKindService(eventKindPlugins);
  }

  @Bean(name = "eventKindTypeService")
  @ConditionalOnMissingBean
  EventKindTypeService eventKindTypeService(
      @NonNull List<EventKindTypePluginIF> eventKindTypePlugins) throws JsonProcessingException {
    return new EventKindTypeService(eventKindTypePlugins);
  }

  @Bean(name = "eventService")
  @ConditionalOnMissingBean
  EventService eventService(
      @NonNull @Qualifier("eventKindService") EventKindServiceIF eventKindService,
      @NonNull @Qualifier("eventKindTypeService") EventKindTypeServiceIF eventKindTypeService) {
    return new EventService(eventKindService, eventKindTypeService);
  }
}
