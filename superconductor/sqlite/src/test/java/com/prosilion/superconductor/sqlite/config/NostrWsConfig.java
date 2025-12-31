package com.prosilion.superconductor.sqlite.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
@ConditionalOnProperty(
    name = "server.ssl.enabled",
    havingValue = "false")
public class NostrWsConfig {

//  @Bean
//  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//  public NostrRelayService nostrRelayService(@Value("${superconductor.relay.url}") String relayUri) throws ExecutionException, InterruptedException {
//    return new NostrRelayService(relayUri);
//  }

//  @Bean
//  EventKindTypePluginIF badgeAwardUpvoteEventKindTypePlugin(
//      @NonNull NotifierService notifierService,
//      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
//    return new BadgeAwardEventKindTypePlugin(
//        notifierService,
//        new EventKindTypePlugin(
//            SuperconductorKindType.UNIT_UPVOTE,
//            eventPlugin));
//  }
//
//  @Bean
//  EventKindTypePluginIF badgeAwardDownvoteEventKindTypePlugin(
//      @NonNull NotifierService notifierService,
//      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
//    return new BadgeAwardEventKindTypePlugin(
//        notifierService,
//        new EventKindTypePlugin(
//            SuperconductorKindType.BADGE_DEFINITION_VOTE,
//            eventPlugin));
//  }
}
