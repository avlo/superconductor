package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.type.SuperconductorKindType;
import java.net.URI;
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
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) throws NoSuchAlgorithmException {
    return new BadgeDefinitionEvent(
        superconductorInstanceIdentity,
        new IdentifierTag(SuperconductorKindType.UPVOTE.getName()),
        new ReferenceTag(URI.create(superconductorRelayUrl)),
        "1");
  }

  @Bean
  BadgeDefinitionEvent downvoteBadgeDefinitionEvent(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String superconductorRelayUrl) throws NoSuchAlgorithmException {
    return new BadgeDefinitionEvent(
        superconductorInstanceIdentity,
        new IdentifierTag(SuperconductorKindType.DOWNVOTE.getName()),
        new ReferenceTag(URI.create(superconductorRelayUrl)),
        "-1");
  }
}
