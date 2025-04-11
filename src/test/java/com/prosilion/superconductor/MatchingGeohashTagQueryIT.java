package com.prosilion.superconductor;

import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.junit.jupiter.api.Test;
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
class MatchingGeohashTagQueryIT {
  private final NostrRelayService nostrRelayService;
  private final static String subscriberId = Factory.generateRandomHex64String();

  @Autowired
  MatchingGeohashTagQueryIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_geohash_tag_query_filter_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      nostrRelayService.createEvent(textMessageEventJson);
      assertFalse(nostrRelayService.getEvents().isEmpty());
    }
  }

  @Test
  void testReqMessagesNoGenericMatch() throws IOException, ExecutionException, InterruptedException {
//    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagString = "textnote-geo-tag-non-existent";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(subscriberId, genericTagString),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  @Test
  void testReqMessagesMatchesGeneric() throws IOException, ExecutionException, InterruptedException {
    String subscriberId = Factory.generateRandomHex64String();
//    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagString = "textnote-geo-tag-1";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(subscriberId, genericTagString),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).isPresent());
    Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow();
//    associated event
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc"));
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains("textnote-geo-tag-1"));
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqJson(@NonNull String uuid, String genericTagString) {
    return "[\"REQ\",\"" + uuid + "\",{\"#g\":[\"" + genericTagString + "\"]}]";
  }
}
