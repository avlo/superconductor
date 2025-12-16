package com.prosilion.superconductor.base;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericVoteEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.OkMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.util.NostrRelayService;
import com.prosilion.superconductor.util.TestKindType;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseBadgeAwardUpvoteEventMessageAuthIT {
  public static final Relay relay = new Relay("ws://localhost:5555");
  private final NostrRelayService nostrRelayService;
  private final BadgeAwardGenericVoteEvent event;

  public BaseBadgeAwardUpvoteEventMessageAuthIT(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String relayUri) throws NostrException {

    this.nostrRelayService = new NostrRelayService(relayUri);
    Identity authorIdentity = Identity.generateRandomIdentity();
    this.event = new BadgeAwardGenericVoteEvent(
        authorIdentity,
        Identity.generateRandomIdentity().getPublicKey(),
        new BadgeDefinitionAwardEvent(
            superconductorInstanceIdentity,
            new IdentifierTag(TestKindType.UNIT_UPVOTE.getName()),
            relay));
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequest() throws IOException, NostrException {
    OkMessage send = nostrRelayService.send(new EventMessage(event));
    assertFalse(send.getFlag());
    assertTrue(send.getMessage().contains("auth-required:"));
  }
}
