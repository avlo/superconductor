package com.prosilion.superconductor.h2db.config;

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
//  EventKindTypePluginIF badgeAwardUpvoteEventKindTypePlugin(
//      @NonNull NotifierService notifierService,
//      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin) {
//    return new BadgeAwardEventKindTypePlugin(
//        notifierService,
//        new EventKindTypePlugin(
//            BadgeDefinitionConfig.UNIT_UPVOTE,
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
//            BadgeDefinitionConfig.UNIT_DOWNVOTE,
//            eventPlugin));
//  }
}
