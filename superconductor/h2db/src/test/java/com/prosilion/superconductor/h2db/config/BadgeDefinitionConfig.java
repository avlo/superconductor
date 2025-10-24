package com.prosilion.superconductor.h2db.config;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.h2db.util.TestKindType.UNIT_DOWNVOTE;
import static com.prosilion.superconductor.h2db.util.TestKindType.UNIT_UPVOTE;

@Configuration
public class BadgeDefinitionConfig {
  @Bean
  @ConditionalOnMissingBean
  DataLoaderJpaIF dataLoaderJpa(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent) {
    return new DataLoaderJpa(eventPlugin, badgeDefinitionUpvoteEvent, badgeDefinitionDownvoteEvent);
  }

  @Bean
  BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent(@NonNull Identity superconductorInstanceIdentity) {
    return new BadgeDefinitionAwardEvent(superconductorInstanceIdentity, new IdentifierTag(UNIT_UPVOTE.getName()));
  }

  @Bean
  BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent(@NonNull Identity superconductorInstanceIdentity) {
    return new BadgeDefinitionAwardEvent(superconductorInstanceIdentity, new IdentifierTag(UNIT_DOWNVOTE.getName()));
  }
}
