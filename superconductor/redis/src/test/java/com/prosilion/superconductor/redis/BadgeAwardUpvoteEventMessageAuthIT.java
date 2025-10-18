package com.prosilion.superconductor.redis;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.OkMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.config.BadgeDefinitionConfig;
import com.prosilion.superconductor.redis.util.BadgeAwardUpvoteRedisEvent;
import com.prosilion.superconductor.redis.util.NostrRelayServiceRedis;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "superconductor.auth.event.kinds=BADGE_AWARD_EVENT",
    "server.port=5557",
    "superconductor.relay.url=ws://localhost:5557",
    "spring.data.redis.port=6380"
})
public class BadgeAwardUpvoteEventMessageAuthIT {
  private final NostrRelayServiceRedis nostrRelayService;
  private final BadgeAwardUpvoteRedisEvent event;

  @Autowired
  BadgeAwardUpvoteEventMessageAuthIT(
      @NonNull Identity superconductorInstanceIdentity,
      @Value("${superconductor.relay.url}") String relayUri) throws NostrException, NoSuchAlgorithmException {

    this.nostrRelayService = new NostrRelayServiceRedis(relayUri);
    this.event = new BadgeAwardUpvoteRedisEvent(
        Identity.generateRandomIdentity(),
        Identity.generateRandomIdentity().getPublicKey(),
        new BadgeDefinitionEvent(
            superconductorInstanceIdentity,
            new IdentifierTag(BadgeDefinitionConfig.UNIT_UPVOTE),
            new ReferenceTag(relayUri),
            "1"));
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequest() throws IOException, NostrException {
    OkMessage send = nostrRelayService.send(new EventMessage(event));
    assertFalse(send.getFlag());
    assertTrue(send.getMessage().contains("auth-required:"));
  }
}
