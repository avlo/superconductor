package com.prosilion.superconductor;

import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingReferencedEventIT {
  private final NostrRelayService nostrRelayService;

  @Autowired
  MatchingReferencedEventIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_reference_event_filter_json_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      assertTrue(nostrRelayService.sendEvent(textMessageEventJson).getFlag());
    }
  }

  @Test
  void testReqMessages() {
    String referencedEventId = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346";
    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(referencedEventId),
        referencedEventId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertFalse(returnedJsonMap.get(Command.EVENT).isEmpty());
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(referencedEventId)));

    //    associated event
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc")));
    assertFalse(returnedJsonMap.get(Command.EOSE).isEmpty());
  }

  private String createReqJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"#e\":[\"" + uuid + "\"]}]";
  }

  @Test
  void testReqNonMatchingReferencedEvent() {
    String subscriberId = Factory.generateRandomHex64String();
    String nonMatchingReferencedEventId = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createNonMatchReferencedEventReqJson(subscriberId, nonMatchingReferencedEventId),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertEquals(0, returnedJsonMap.get(Command.EVENT).size());
    assertFalse(returnedJsonMap.get(Command.EOSE).isEmpty());
  }

  private String createNonMatchReferencedEventReqJson(@NonNull String subscriberId, @NonNull String nonMatchingEventId) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"#e\":[\"" + nonMatchingEventId + "\"]}]";
  }
}
