package com.prosilion.superconductor.sqlite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.ReferencedEventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.sqlite.util.Factory;
import com.prosilion.superconductor.sqlite.util.NostrRelayService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.sqlite.TextNoteEventMessageSqliteIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingReferencedEventIT {
  private final NostrRelayService nostrRelayService;
  private final String eventId = Factory.generateRandomHex64String();

  @Autowired
  MatchingReferencedEventIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    assertTrue(
        nostrRelayService.send(
                (EventMessage) BaseMessageDecoder.decode(getEvent()))
            .getFlag());
  }

  @Test
  void testReqMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    String referencedEventId = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346";

    EventTag eventTag = new EventTag(referencedEventId);
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new ReferencedEventFilter(eventTag)));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());

    assertTrue(returnedEvents.stream()
        .map(event -> event.getTags().stream()
            .filter(EventTag.class::isInstance)
            .map(EventTag.class::cast)
            .map(EventTag::getIdEvent).findFirst()).anyMatch(s -> s.orElseThrow().equals(referencedEventId)));

    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }

  @Test
  void testReqNonMatchingReferencedEvent() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    String nonMatchingReferencedEventId = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new ReferencedEventFilter(new EventTag(nonMatchingReferencedEventId))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

    assertTrue(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());
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
        "        \"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"e\",\n" +
        "        \"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"\n" +
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
