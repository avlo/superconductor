package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventService;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import com.prosilion.superconductor.base.service.event.service.EventKindService;
import com.prosilion.superconductor.base.service.event.service.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.type.CanonicalEventKindPlugin;
import com.prosilion.superconductor.base.service.event.type.DeleteEventKindPlugin;
import com.prosilion.superconductor.base.service.event.type.DeleteEventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventKindPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.base.service.request.NotifierService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
public class EventKindServiceConfig {
  @Bean
  @ConditionalOnMissingBean
  EventKindServiceIF eventKindServiceIF(@NonNull List<EventKindPluginIF> eventKindPlugins) {
    return new EventKindService(eventKindPlugins);
  }

  @Bean
  EventKindPluginIF textNoteEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    log.debug("loaded canonical textNoteEventKindPlugin bean");
    return new CanonicalEventKindPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.TEXT_NOTE, eventPlugin));
  }

  @Bean
  @ConditionalOnMissingBean
  EventServiceIF eventService(@NonNull @Qualifier("eventKindServiceIF") EventKindServiceIF eventKindService) {
    return new EventService(eventKindService);
  }

  @Bean
  EventKindPluginIF deleteEventKindPlugin(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull CacheServiceIF cacheServiceIF) {
    return new DeleteEventKindPlugin(
        new EventKindPlugin(
            Kind.DELETION,
            eventPlugin),
        new DeleteEventPlugin(cacheServiceIF));
  }
}
