package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.service.event.type.CanonicalEventKindPlugin;
import com.prosilion.superconductor.service.event.type.EventKindPlugin;
import com.prosilion.superconductor.service.event.type.EventPluginIF;
import com.prosilion.superconductor.service.request.NotifierService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class EventConfig {

  @Bean
//      TODO: conditional missing bean below needs review
//  @ConditionalOnMissingBean
  EventKindPluginIF<Kind> textNoteEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPluginIF eventPlugin) {
    return new CanonicalEventKindPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.TEXT_NOTE, eventPlugin));
  }
}
