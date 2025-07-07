package com.prosilion.superconductor.service.message.event.config;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePlugin;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.service.event.type.BadgeAwardEventKindTypePlugin;
import com.prosilion.superconductor.service.event.type.EventPluginIF;
import com.prosilion.superconductor.service.event.type.SuperconductorKindType;
import com.prosilion.superconductor.service.request.NotifierService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@AutoConfiguration
public class BadgeAwardEventConfig {
  @Bean
  EventKindTypePluginIF<KindTypeIF> badgeAwardUpvoteEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPluginIF eventPlugin) {
    return new BadgeAwardEventKindTypePlugin(
        notifierService,
        new EventKindTypePlugin(
            SuperconductorKindType.UPVOTE,
            eventPlugin));
  }

  @Bean
  EventKindTypePluginIF<KindTypeIF> badgeAwardDownvoteEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPluginIF eventPlugin) {
    return new BadgeAwardEventKindTypePlugin(
        notifierService,
        new EventKindTypePlugin(
            SuperconductorKindType.DOWNVOTE,
            eventPlugin));
  }
}
