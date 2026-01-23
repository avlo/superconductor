package com.prosilion.superconductor.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.GenericTagQuery;
import com.prosilion.nostr.filter.tag.GenericTagQueryFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.GeohashTag;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.base.util.NostrRelayService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public abstract class BaseMatchingMultipleGenericTagQuerySingleLetterIT {
  private final NostrRelayService nostrRelayService;
  private final String eventId;
  private final String genericTagStringG;
  private final String genericTagStringH;

  public BaseMatchingMultipleGenericTagQuerySingleLetterIT(@NonNull String relayUrl) throws IOException {
    this.eventId = Factory.generateRandomHex64String();
    this.genericTagStringG = Factory.generateRandomHex64String();
    this.genericTagStringH = Factory.generateRandomHex64String();
    this.nostrRelayService = new NostrRelayService(relayUrl);
    assertTrue(
        nostrRelayService.send(
                (EventMessage) BaseMessageDecoder.decode(getEvent()))
            .getFlag());
  }

  @Test
  @Order(0)
  void testReqMessagesMissingOneGenericMatch() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagStringGMissing = "textnote-geo-tag-2";
    String genericTagStringHPresent = "hash-tag-1";

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new GenericTagQueryFilter(
                new GenericTagQuery("#g", genericTagStringGMissing)),
            new GenericTagQueryFilter(
                new GenericTagQuery("#h", genericTagStringHPresent))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = BaseTextNoteEventMessageIT.getEventIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertTrue(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());
  }

  @Test
  @Order(1)
  void testReqMessagesMissingBothGenericMatch() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagStringGMissing = "textnote-geo-tag-2";
    String genericTagStringHPresent = "hash-tag-2";

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new GenericTagQueryFilter(
                new GenericTagQuery("#g", genericTagStringGMissing)),
            new GenericTagQueryFilter(
                new GenericTagQuery("#h", genericTagStringHPresent))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = BaseTextNoteEventMessageIT.getEventIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertTrue(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());
  }

  @Test
  @Order(2)
  void testReqMessagesMatchesGeneric() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new GenericTagQueryFilter(
                new GenericTagQuery("#g", genericTagStringG)),
            new GenericTagQueryFilter(
                new GenericTagQuery("#h", genericTagStringH))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = BaseTextNoteEventMessageIT.getEventIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());

    //    associated event
    assertTrue(returnedEvents.stream().map(EventIF::getId).anyMatch(s -> s.contains(eventId)));
    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }

  @Test
  @Order(3)
  void testReqMessagesMatchesGenericWithSpaces() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new GenericTagQueryFilter(
                new GenericTagQuery("#g", genericTagStringG)),
            new GenericTagQueryFilter(
                new GenericTagQuery("#h", genericTagStringH))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = BaseTextNoteEventMessageIT.getEventIFs(returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    //    associated event
    assertTrue(returnedEvents.stream().anyMatch(s -> s.getId().equals((eventId))));
    assertTrue(returnedEvents.stream().anyMatch(s -> s.getTags().stream()
        .filter(GeohashTag.class::isInstance)
        .map(GeohashTag.class::cast)
        .anyMatch(tag -> tag.getLocation().equals(genericTagStringG))));

    assertEquals(1, (long) returnedEvents.size());

    assertEquals(1, returnedEvents.size());
    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }

  private String getEvent() {
    return "[\n" +
        "  \"EVENT\",\n" +
        "  {\n" +
        "    \"content\": \"matching generic tag query filter test\",\n" +
        "    \"id\": \"" + eventId + "\",\n" +
        "    \"kind\": 1,\n" +
        "    \"created_at\": 1111111111111,\n" +
        "    \"pubkey\": \"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\",\n" +
        "    \"tags\": [\n" +
        "      [\n" +
        "        \"g\",\n" +
        "        \"" + genericTagStringG + "\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"h\",\n" +
        "        \"" + genericTagStringH + "\"\n" +
        "      ]\n" +
        "    ],\n" +
        "    \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"\n" +
        "  }\n" +
        "]\n";
  }
}
