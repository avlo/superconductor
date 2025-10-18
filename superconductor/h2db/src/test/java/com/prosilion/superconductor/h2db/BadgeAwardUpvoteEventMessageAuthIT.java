package com.prosilion.superconductor.h2db;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardDefinitionEvent;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.OkMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.h2db.util.BadgeAwardUpvoteEvent;
import com.prosilion.superconductor.h2db.util.NostrRelayService;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "superconductor.auth.event.kinds=BADGE_AWARD_EVENT",
    "server.port=5556",
    "superconductor.relay.url=ws://localhost:5556"
})
public class BadgeAwardUpvoteEventMessageAuthIT {
  private final NostrRelayService nostrRelayService;
  private final BadgeAwardUpvoteEvent event;

  @Autowired
  BadgeAwardUpvoteEventMessageAuthIT(
      @NonNull NostrRelayService nostrRelayService,
      @NonNull @Qualifier("upvoteBadgeDefinitionEvent") BadgeAwardDefinitionEvent upvoteBadgeDefinitionEvent) throws NostrException, NoSuchAlgorithmException {
    this.nostrRelayService = nostrRelayService;
    this.event = new BadgeAwardUpvoteEvent(
        Identity.generateRandomIdentity(),
        Identity.generateRandomIdentity().getPublicKey(),
        upvoteBadgeDefinitionEvent);
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequest() throws IOException, NostrException {
    OkMessage send = nostrRelayService.send(new EventMessage(event));
    assertFalse(send.getFlag());
    assertTrue(send.getMessage().contains("auth-required:"));
    nostrRelayService.disconnect();
  }
}
