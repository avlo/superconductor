package com.prosilion.superconductor.redis.config;

import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePlugin;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.base.service.request.NotifierService;
import com.prosilion.superconductor.redis.util.BadgeAwardEventKindTypeRedisPlugin;
import com.prosilion.superconductor.util.TestKindType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.util.TestKindType.UNIT_DOWNVOTE;

//@Lazy
@Configuration
@ConditionalOnProperty(
    name = "server.ssl.enabled",
    havingValue = "false")
public class NostrRedisWsTestConfig {

  @Bean
  EventKindTypePluginIF badgeAwardUpvoteEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new BadgeAwardEventKindTypeRedisPlugin(
        notifierService,
        new EventKindTypePlugin(
            TestKindType.UNIT_UPVOTE,
            eventPlugin));
  }

  @Bean
  EventKindTypePluginIF badgeAwardDownvoteEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new BadgeAwardEventKindTypeRedisPlugin(
        notifierService,
        new EventKindTypePlugin(
            UNIT_DOWNVOTE,
            eventPlugin));
  }
}
