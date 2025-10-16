package com.prosilion.superconductor.h2db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.ExternalIdentityTagFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.h2db.util.Factory;
import com.prosilion.superconductor.h2db.util.NostrRelayService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.h2db.TextNoteEventMessageH2dbIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingExternalIdentityTagIT {
  private final NostrRelayService nostrRelayService;
  private final String eventId = Factory.generateRandomHex64String();
  private final PublicKey publicKey = Identity.generateRandomIdentity().getPublicKey();
  private final String UNIT_UPVOTE = "UNIT_UPVOTE";
  private final String formula = "+1";

  @Autowired
  MatchingExternalIdentityTagIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    assertTrue(
        nostrRelayService.send(
                (EventMessage) BaseMessageDecoder.decode(getEvent()))
            .getFlag());
  }

  @Test
  void testReqMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    IdentifierTag identifierTag = new IdentifierTag(UNIT_UPVOTE);

    ExternalIdentityTag externalIdentityTag = new ExternalIdentityTag(
        Kind.BADGE_AWARD_EVENT, identifierTag, formula
    );

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(new ExternalIdentityTagFilter(externalIdentityTag)));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);
    log.debug("okMessage:");
    log.debug("  " + returnedEvents);

    assertTrue(returnedEvents.stream().anyMatch(event ->
        event.getTags().stream().anyMatch(baseTag -> baseTag.equals(externalIdentityTag))));

    assertTrue(returnedEvents.stream().anyMatch(s -> s.getTags().stream()
        .filter(ExternalIdentityTag.class::isInstance)
        .map(ExternalIdentityTag.class::cast)
        .anyMatch(tag -> {
          Assertions.assertNotNull(tag.getIdentifierTag());
          return tag.getIdentifierTag().equals(identifierTag);
        })));
  }

  private String getEvent() {
    return "[\n" +
        "  \"EVENT\",\n" +
        "  {\n" +
        "    \"content\": \"matching address tag filter test\",\n" +
        "    \"id\": \"" + eventId + "\",\n" +
        "    \"kind\": 1,\n" +
        "    \"created_at\": 1111111111111,\n" +
        "    \"pubkey\": \"" + publicKey + "\",\n" +
        "    \"tags\": [\n" +
        "      [\n" +
        "        \"i\",\n" +
        "        \"8:" + UNIT_UPVOTE + ":" + formula + "\"\n" +
        "      ]\n" +
        "    ],\n" +
        "    \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"\n" +
        "  }\n" +
        "]\n";
  }
}
