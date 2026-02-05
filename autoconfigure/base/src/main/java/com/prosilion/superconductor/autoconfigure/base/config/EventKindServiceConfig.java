package com.prosilion.superconductor.autoconfigure.base.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventService;
import com.prosilion.superconductor.base.service.event.kind.EventKindService;
import com.prosilion.superconductor.base.service.event.kind.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeService;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.CanonicalEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.DeleteEventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.DeleteEventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPlugin;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
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
      @NonNull List<EventKindPluginIF<? extends BaseEvent>> eventKindPlugins,
      @NonNull Map<String, String> kindClassStringMap) {
    return new EventKindService(eventKindPlugins);
  }

  @Bean(name = "eventKindTypeService")
  @ConditionalOnMissingBean
  EventKindTypeService eventKindTypeService(
      @NonNull List<EventKindTypePluginIF<? extends BaseEvent>> eventKindTypePlugins) throws JsonProcessingException {
    return new EventKindTypeService(eventKindTypePlugins);
  }

  @Bean
//  @ConditionalOnMissingBean
  CanonicalEventKindPlugin textNoteEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    log.debug("loaded canonical textNoteEventKindPlugin bean");
    return new CanonicalEventKindPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.TEXT_NOTE, eventPlugin));
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
  DeleteEventKindPlugin deleteEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheServiceIF cacheService) {
    return new DeleteEventKindPlugin(
        new EventKindPlugin(
            Kind.DELETION,
            eventPlugin),
        new DeleteEventPlugin(cacheService));
  }
}
