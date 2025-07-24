package com.prosilion.superconductor.sqlite.config;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePlugin;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.base.service.event.type.SuperconductorKindType;
import com.prosilion.superconductor.base.service.request.NotifierService;
import com.prosilion.superconductor.sqlite.util.BadgeAwardEventKindTypePlugin;
import com.prosilion.superconductor.sqlite.util.NostrRelayService;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;

@Lazy
@Configuration
@ConditionalOnProperty(
    name = "server.ssl.enabled",
    havingValue = "false")
public class NostrWsConfig {

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public NostrRelayService nostrRelayService(@Value("${superconductor.relay.url}") String relayUri) throws ExecutionException, InterruptedException {
    return new NostrRelayService(relayUri);
  }

  @Bean
  EventKindTypePluginIF<KindTypeIF> badgeAwardUpvoteEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new BadgeAwardEventKindTypePlugin(
        notifierService,
        new EventKindTypePlugin(
            SuperconductorKindType.UPVOTE,
            eventPlugin));
  }

  @Bean
  EventKindTypePluginIF<KindTypeIF> badgeAwardDownvoteEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
    return new BadgeAwardEventKindTypePlugin(
        notifierService,
        new EventKindTypePlugin(
            SuperconductorKindType.DOWNVOTE,
            eventPlugin));
  }
}
