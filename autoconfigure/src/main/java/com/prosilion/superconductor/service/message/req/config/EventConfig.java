package com.prosilion.superconductor.service.message.req.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.service.event.type.CanonicalEventKindPlugin;
import com.prosilion.superconductor.service.event.type.EventKindPlugin;
import com.prosilion.superconductor.service.event.type.EventPluginIF;
import com.prosilion.superconductor.service.request.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
public class EventConfig {

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
