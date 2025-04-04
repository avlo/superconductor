package com.prosilion.superconductor;

import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
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
@TestInstance(Lifecycle.PER_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class MatchingMultipleGenericTagQuerySingleLetterIT {
  private final NostrRelayService nostrRelayService;
  private final String textMessageEventJson;
  private final String uuidPrefix;

  @Autowired
  MatchingMultipleGenericTagQuerySingleLetterIT(@NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix
  ) throws IOException {
    this.nostrRelayService = nostrRelayService;
    this.uuidPrefix = uuidPrefix;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_multiple_generic_tag_query_filter_single_letter_json_input.txt"))) {
      this.textMessageEventJson = lines.collect(Collectors.joining("\n"));
    }
  }

  @BeforeEach
  public void setup() throws IOException {
    log.debug("setup() send event:\n  {}", textMessageEventJson);
    nostrRelayService.createEvent(textMessageEventJson);
    assertFalse(nostrRelayService.getEvents().isEmpty());
  }

  @Test
  @Order(0)
  void testReqMessagesMissingOneGenericMatch() throws IOException, ExecutionException, InterruptedException {
    String subscriberId = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346";
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
    String subscriberId = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346";
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
    String subscriberId = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346";
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
    String subscriberId = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346";
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
