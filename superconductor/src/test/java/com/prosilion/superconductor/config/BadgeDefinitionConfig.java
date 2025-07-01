package com.prosilion.superconductor.config;

import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class BadgeDefinitionConfig {

  @Bean
  Identity superconductorInstanceIdentity(@NonNull @Value("${superconductor.key.private}") String privateKey) {
    return Identity.create(privateKey);
  }

  @Bean
  BadgeDefinitionEvent upvoteBadgeDefinitionEvent(@NonNull Identity superconductorInstanceIdentity) throws NoSuchAlgorithmException, IOException {
    return new BadgeDefinitionEvent(
        superconductorInstanceIdentity,
        new IdentifierTag("UPVOTE"),
        "1");
  }

  @Bean
  BadgeDefinitionEvent downvoteBadgeDefinitionEvent(@NonNull Identity superconductorInstanceIdentity) throws NoSuchAlgorithmException, IOException {
    return new BadgeDefinitionEvent(
        superconductorInstanceIdentity,
        new IdentifierTag("DOWNVOTE"),
        "-1");
  }
}
