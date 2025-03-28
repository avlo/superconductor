package com.prosilion.superconductor;

import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
      nostrRelayService.createEvent(textMessageEventJson);
      assertFalse(nostrRelayService.getEvents().isEmpty());
    }
  }

  @Test
  @Order(0)
  void testReqMessagesMissingOneGenericMatch() throws IOException, ExecutionException, InterruptedException {
    String subscriberId = Factory.generateRandomHex64String();
//    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagStringGMissing = "textnote-geo-tag-2";
    String genericTagStringHPresent = "hash-tag-1";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(subscriberId, genericTagStringGMissing, genericTagStringHPresent),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  @Test
  @Order(1)
  void testReqMessagesMissingBothGenericMatch() throws IOException, ExecutionException, InterruptedException {
    String subscriberId = Factory.generateRandomHex64String();
//    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagStringGMissing = "textnote-geo-tag-2";
    String genericTagStringHPresent = "hash-tag-2";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(subscriberId, genericTagStringGMissing, genericTagStringHPresent),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  @Test
  @Order(2)
  void testReqMessagesMatchesGeneric() throws IOException, ExecutionException, InterruptedException {
    String subscriberId = Factory.generateRandomHex64String();
//    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagStringG = "textnote-geo-tag-1";
    String genericTagStringH = "hash-tag-1";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(subscriberId, genericTagStringG, genericTagStringH),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).isPresent());
    Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow();
//    associated event
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc"));
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqJson(@NonNull String uuid, String genericTagStringG, String genericTagStringH) {
    String reqJson = "[\"REQ\",\"" + uuid +
        "\",{" +
        "\"#g\":[\"" + genericTagStringG + "\"]," +
        "\"#h\":[\"" + genericTagStringH + "\"]" +
        "}]";
    return reqJson;
  }

  @Test
  @Order(3)
  void testReqMessagesMatchesGenericWithSpaces() throws IOException, ExecutionException, InterruptedException {
    String subscriberId = Factory.generateRandomHex64String();
//    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagStringG = "textnote-geo-tag-1";
    String genericTagStringH = "hash-tag-1";
    String genericTagStringI = "random i tag with spaces";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqTagWResultSpaces(subscriberId, genericTagStringG, genericTagStringH, genericTagStringI),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).isPresent());
    Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow();
//    associated event
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc"));
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains("textnote-geo-tag-1"));
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains("hash-tag-1"));
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains("random i tag with spaces"));
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqTagWResultSpaces(@NonNull String uuid, String genericTagStringG, String genericTagStringH, String genericTagStringI) {
    String reqJson = "[\"REQ\",\"" + uuid +
        "\",{" +
        "\"#g\":[\"" + genericTagStringG + "\"]," +
        "\"#i\":[\"" + genericTagStringI + "\"]," +
        "\"#h\":[\"" + genericTagStringH + "\"]" +
        "}]";
    return reqJson;
  }
}
