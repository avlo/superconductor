package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.enums.NostrException;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.GenericTagQuery;
import com.prosilion.nostr.filter.tag.GenericTagQueryFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.nostr.tag.GeohashTag;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.EventMessageIT.getGenericEventKindIFs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class MatchingMultipleGenericTagQuerySingleLetterIT {
  private final NostrRelayService nostrRelayService;

  @Autowired
  MatchingMultipleGenericTagQuerySingleLetterIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_multiple_generic_tag_query_filter_single_letter_json_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      assertTrue(
          nostrRelayService.send(
                  (EventMessage) BaseMessageDecoder.decode(textMessageEventJson))
              .getFlag());
    }
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
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

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
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

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
    String genericTagStringG = "textnote-geo-tag-1";
    String genericTagStringH = "hash-tag-1";
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new GenericTagQueryFilter(
                new GenericTagQuery("#g", genericTagStringG)),
            new GenericTagQueryFilter(
                new GenericTagQuery("#h", genericTagStringH))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());

    //    associated event
    assertTrue(returnedEvents.stream().map(GenericEventKindIF::getId).anyMatch(s -> s.contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e590005")));
    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }

  @Test
  @Order(3)
  void testReqMessagesMatchesGenericWithSpaces() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagStringG = "textnote-geo-tag-1";
    String genericTagStringH = "hash-tag-1";
    String genericTagStringI = "random i tag with spaces";

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new GenericTagQueryFilter(
                new GenericTagQuery("#g", genericTagStringG)),
            new GenericTagQueryFilter(
                new GenericTagQuery("#h", genericTagStringH)),
            new GenericTagQueryFilter(
                new GenericTagQuery("#i", genericTagStringI))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    //    associated event
    assertTrue(returnedEvents.stream().anyMatch(s -> s.getId().equals(("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e590005"))));
    assertTrue(returnedEvents.stream().anyMatch(s -> s.getTags().stream()
        .filter(GeohashTag.class::isInstance)
        .map(GeohashTag.class::cast)
        .anyMatch(tag -> tag.getLocation().equals(genericTagStringG))));

    assertEquals(1, returnedEvents.stream().map(s -> s.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .map(tag -> tag.getAttributes().stream().map(attribute -> Stream.of(attribute.getValue().toString())
            .filter(s1 -> s1.equals(genericTagStringH))))).count());

    assertEquals(1, returnedEvents.stream().map(s -> s.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .map(tag -> tag.getAttributes().stream().map(attribute -> Stream.of(attribute.getValue().toString())
            .filter(s1 -> s1.equals(genericTagStringI))))).count());

    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }
}
