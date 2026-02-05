package com.prosilion.superconductor.sqlite.config;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class BadgeDefinitionConfig {
  private final static String UNIT_UPVOTE = "UNIT_UPVOTE";
  private final static String UNIT_DOWNVOTE = "UNIT_DOWNVOTE";

  @Bean
  @ConditionalOnMissingBean
  DataLoaderJpaIF dataLoaderJpa(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent) {
    return new DataLoaderJpa(eventPlugin, badgeDefinitionUpvoteEvent, badgeDefinitionDownvoteEvent);
  }

  @Bean(name = "badgeDefinitionUpvoteEvent")
  BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) {
    return new BadgeDefinitionGenericEvent(superconductorInstanceIdentity, new IdentifierTag(UNIT_UPVOTE), new Relay(superconductorRelayUrl));
  }

  @Bean(name = "badgeDefinitionDownvoteEvent")
  BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) {
    return new BadgeDefinitionGenericEvent(superconductorInstanceIdentity, new IdentifierTag(UNIT_DOWNVOTE), new Relay(superconductorRelayUrl));
  }
}
