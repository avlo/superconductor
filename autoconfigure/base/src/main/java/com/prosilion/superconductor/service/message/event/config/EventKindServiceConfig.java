package com.prosilion.superconductor.service.message.event.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.superconductor.base.service.event.service.EventKindService;
import com.prosilion.superconductor.base.service.event.service.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.service.EventKindTypeService;
import com.prosilion.superconductor.base.service.event.service.EventKindTypeServiceIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.CacheIF;
import com.prosilion.superconductor.base.service.event.type.CanonicalEventKindPlugin;
import com.prosilion.superconductor.base.service.event.type.EventKindPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPlugin;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.base.service.request.NotifierService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
public class EventKindServiceConfig {

  @Bean
  @ConditionalOnMissingBean
  EventKindServiceIF eventKindService(@NonNull List<EventKindPluginIF<Kind>> eventKindPlugins) {
    return new EventKindService(eventKindPlugins);
  }

  @Bean
  @ConditionalOnMissingBean
  EventKindTypeServiceIF eventKindTypeServiceIF(@NonNull List<EventKindTypePluginIF<KindTypeIF>> eventKindTypePlugins) {
    return new EventKindTypeService(eventKindTypePlugins);
  }

  @Bean
  EventPluginIF eventPlugin(@NonNull CacheIF cacheIF) {
    return new EventPlugin(cacheIF);
  }

  @Bean
//  @ConditionalOnMissingBean
  EventKindPluginIF<Kind> textNoteEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPluginIF eventPlugin) {
    log.debug("loaded canonical textNoteEventKindPlugin bean");
    return new CanonicalEventKindPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.TEXT_NOTE, eventPlugin));
  }
}
