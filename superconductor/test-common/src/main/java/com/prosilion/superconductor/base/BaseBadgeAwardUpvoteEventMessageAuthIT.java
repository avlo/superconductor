package com.prosilion.superconductor.base;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.OkMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.superconductor.util.Factory;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseBadgeAwardUpvoteEventMessageAuthIT {
  public static final String IDENTIFIER_TAG_UUID = Factory.generateRandomHex64String();

  public static final Relay relay = new Relay("ws://localhost:5555");
  private final BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> event;
  private final String relayUrl;

  public BaseBadgeAwardUpvoteEventMessageAuthIT(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull String relayUrl) throws NostrException {
    this.relayUrl = relayUrl;
    Identity authorIdentity = Identity.generateRandomIdentity();
    this.event = new BadgeAwardGenericEvent<>(
        authorIdentity,
        Identity.generateRandomIdentity().getPublicKey(),
        relay,
        new BadgeDefinitionGenericEvent(
            superconductorInstanceIdentity,
            new IdentifierTag(IDENTIFIER_TAG_UUID),
            relay));
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequest() throws IOException, NostrException {
    OkMessage send = new NostrEventPublisher(relayUrl).send(new EventMessage(event));
    assertFalse(send.getFlag());
    assertTrue(send.getMessage().contains("auth-required:"));
  }
}
