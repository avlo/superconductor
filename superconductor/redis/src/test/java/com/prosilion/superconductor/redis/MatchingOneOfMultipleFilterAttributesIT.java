package com.prosilion.superconductor.redis;

import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.ReferencedEventFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.redis.util.Factory;
import com.prosilion.superconductor.redis.util.NostrRelayServiceRedis;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.redis.TextNoteEventMessageRedisIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingOneOfMultipleFilterAttributesIT {
  private final NostrRelayServiceRedis nostrRelayService;
  private final String eventId = Factory.generateRandomHex64String();
  private final String referenceEventId = Factory.generateRandomHex64String();
  private final String referencePubKey = Factory.generateRandomHex64String();

  @Autowired
  MatchingOneOfMultipleFilterAttributesIT(@NonNull NostrRelayServiceRedis nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    assertTrue(
        nostrRelayService.send(
                (EventMessage) BaseMessageDecoder.decode(getEvent()))
            .getFlag());
  }

  @Test
  void testMatchingOneOfMultipleReferencedEvents() throws IOException {
    String subscriberId = Factory.generateRandomHex64String();

    String referencedEventIdNoMatch = "0000000000000000000000000000000000000000000000000000000000000000";

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new ReferencedEventFilter(
                new EventTag(referenceEventId)),
            new ReferencedEventFilter(
                new EventTag(referencedEventIdNoMatch))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());

    assertTrue(returnedEvents.stream().map(EventIF::getId).anyMatch(s -> s.contains(eventId)));
  }

  @Test
  void testMatchingOneOfMultipleReferencedPublicKeys() throws IOException {
    String subscriberId = Factory.generateRandomHex64String();
    String referencedPubkeyNoMatch = "0000000000000000000000000000000000000000000000000000000000000000";

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new ReferencedPublicKeyFilter(
                new PubKeyTag(new PublicKey(referencePubKey))),
            new ReferencedPublicKeyFilter(
                new PubKeyTag(new PublicKey(referencedPubkeyNoMatch)))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());

    assertTrue(returnedEvents.stream().map(EventIF::getId).anyMatch(s -> s.contains(eventId)));
  }

  private String getEvent() {
    return "[\n" +
        "  \"EVENT\",\n" +
        "  {\n" +
        "    \"content\": \"matching identity filter test\",\n" +
        "    \"id\":\"" + eventId + "\",\n" +
        "    \"kind\": 1,\n" +
        "    \"created_at\": 1111111111111,\n" +
        "    \"pubkey\": \"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\",\n" +
        "    \"tags\": [\n" +
        "      [\n" +
        "        \"a\",\n" +
        "        \"30023:f7234bd4c1394dda46d09f35bd384dd30cc552ad5541990f98844fb06676e9ca:abcd\",\n" +
        "        \"wss://nostr.example.com\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"custom-tag\",\n" +
        "        \"custom-tag random value\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"p\",\n" +
        "        \"" + referencePubKey + "\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"e\",\n" +
        "        \"" + referenceEventId + "\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"g\",\n" +
        "        \"textnote geo-tag-1\"\n" +
        "      ]\n" +
        "    ],\n" +
        "    \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"\n" +
        "  }\n" +
        "]\n";
  }
}
