package com.prosilion.superconductor.service.message.event.config;

import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.RelaysTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.dto.GenericEventKindDto;
import com.prosilion.superconductor.service.event.type.EventPluginIF;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@AutoConfiguration
public class BadgeDefinitionConfig {

  @Bean
  @ConditionalOnMissingBean
  String superconductorRelayUrl(@NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl) {
    return superconductorRelayUrl;
  }

  @Bean
  @ConditionalOnMissingBean
  Identity superconductorInstanceIdentity(@NonNull @Value("${superconductor.key.private}") String privateKey) {
    return Identity.create(privateKey);
  }

  @Bean
  BadgeDefinitionEvent upvoteBadgeDefinitionEvent(
      @NonNull EventPluginIF eventPlugin,
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) throws NoSuchAlgorithmException {

    BadgeDefinitionEvent upvateBadgeDefinitionEvent = new BadgeDefinitionEvent(
        superconductorInstanceIdentity,
        new IdentifierTag("UPVOTE"),
        new RelaysTag(new Relay(superconductorRelayUrl)),
        "1");

    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(upvateBadgeDefinitionEvent).convertBaseEventToGenericEventKindIF());

    return upvateBadgeDefinitionEvent;
  }

  @Bean
  BadgeDefinitionEvent downvoteBadgeDefinitionEvent(
      @NonNull EventPluginIF eventPlugin,
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) throws NoSuchAlgorithmException {

    BadgeDefinitionEvent badgeDefinitionEvent = new BadgeDefinitionEvent(
        superconductorInstanceIdentity,
        new IdentifierTag("DOWNVOTE"),
        new RelaysTag(new Relay(superconductorRelayUrl)),
        "-1");

    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(badgeDefinitionEvent).convertBaseEventToGenericEventKindIF());

    return badgeDefinitionEvent;
  }
}
