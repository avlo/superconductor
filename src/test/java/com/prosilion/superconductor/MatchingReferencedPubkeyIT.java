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
class MatchingReferencedPubkeyIT {
  private final NostrRelayService nostrRelayService;

  @Autowired
  MatchingReferencedPubkeyIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_reference_event_filter_json_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      nostrRelayService.createEvent(textMessageEventJson);
      assertFalse(nostrRelayService.getEvents().isEmpty());
    }
  }

  @Test
  void testReqMessages() throws IOException, ExecutionException, InterruptedException {
    String referencedPubKey = "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(referencedPubKey),
        referencedPubKey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).isPresent());
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(referencedPubKey));

//    associated event
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc"));
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"#p\":[\"" + uuid + "\"]}]";
  }

  @Test
  void testReqNonMatchingReferencedPubkey() throws IOException, ExecutionException, InterruptedException {
    String subscriberId = Factory.generateRandomHex64String();
    String nonMatchingReferencedPubKey = "cccd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createNonMatchReferencedEventReqJson(subscriberId, nonMatchingReferencedPubKey),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createNonMatchReferencedEventReqJson(@NonNull String subscriberId, @NonNull String nonMatchingRefPubKey) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"#p\":[\"" + nonMatchingRefPubKey + "\"]}]";
  }
}
