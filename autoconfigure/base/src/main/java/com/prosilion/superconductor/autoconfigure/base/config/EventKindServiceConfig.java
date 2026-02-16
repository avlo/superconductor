package com.prosilion.superconductor.autoconfigure.base.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.service.event.EventService;
import com.prosilion.superconductor.base.service.event.kind.EventKindService;
import com.prosilion.superconductor.base.service.event.kind.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeService;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.BadgeDefinitionGenericEventKindRedisPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.MaterializedEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
      @NonNull List<EventKindPluginIF> eventKindPlugins) {
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

  @Bean
  @ConditionalOnMissingBean
  EventKindPluginIF badgeDefinitionGenericEventKindPlugin(
      @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) {
    BadgeDefinitionGenericEventKindRedisPlugin badgeDefinitionGenericEventKindRedisPlugin =
        new BadgeDefinitionGenericEventKindRedisPlugin(
            superconductorRelayUrl,
            new MaterializedEventKindPlugin(
                Kind.BADGE_DEFINITION_EVENT,
                eventPlugin,
                cacheBadgeDefinitionGenericEventService),
            cacheBadgeDefinitionGenericEventService);
    return badgeDefinitionGenericEventKindRedisPlugin;
  }
}
