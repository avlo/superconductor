package com.prosilion.superconductor.util.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePlugin;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.service.event.type.CanonicalEventKindPlugin;
import com.prosilion.superconductor.service.event.type.EventKindPlugin;
import com.prosilion.superconductor.service.event.type.EventPluginIF;
import com.prosilion.superconductor.service.request.NotifierService;
import com.prosilion.superconductor.util.BadgeAwardEventKindTypePlugin;
import com.prosilion.superconductor.util.TestKindType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class EventKindTypeTestConfig {

  @Bean
//  @ConditionalOnMissingBean
  EventKindPluginIF<Kind> contactListEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPluginIF eventPlugin) {
    return new CanonicalEventKindPlugin(
        notifierService,
        new EventKindPlugin(
            Kind.CLASSIFIED_LISTING, eventPlugin));
  }

  @Bean
//  @ConditionalOnMissingBean
  EventKindTypePluginIF<KindTypeIF> badgeAwardUpvoteEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPluginIF eventPlugin) {
    return new BadgeAwardEventKindTypePlugin(
        notifierService,
        new EventKindTypePlugin(
            TestKindType.UPVOTE,
            new EventKindPlugin(
                Kind.BADGE_AWARD_EVENT,
                eventPlugin)));
  }
}
