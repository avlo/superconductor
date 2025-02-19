package com.prosilion.superconductor;

import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("test")
class MatchingOneOfMultipleFilterAttributesIT {
  private final NostrRelayService nostrRelayService;
  private final String textMessageEventJson;
  private final String uuidPrefix;

  @Autowired
  MatchingOneOfMultipleFilterAttributesIT(@NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix
  ) throws IOException {
    this.nostrRelayService = nostrRelayService;
    this.uuidPrefix = uuidPrefix;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_reference_event_filter_json_input.txt"))) {
      this.textMessageEventJson = lines.collect(Collectors.joining("\n"));
    }
  }

  @BeforeAll
  public void setup() throws IOException {
    log.debug("setup() send event:\n  {}", textMessageEventJson);
    nostrRelayService.createEvent(textMessageEventJson);
    assertFalse(nostrRelayService.getEvents().isEmpty());
  }

  @Test
  void testMatchingOneOfMultipleReferencedEvents() throws IOException, ExecutionException, InterruptedException {
    String referencedEventIdMatch = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346";
    String referencedEventIdNoMatch = "0000000000000000000000000000000000000000000000000000000000000000";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReferencedEventReqJson(referencedEventIdMatch, referencedEventIdNoMatch),
        referencedEventIdMatch
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().isPresent());
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(referencedEventIdMatch));

    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc"));
    assertTrue(Optional.of(returnedJsonMap.get(Command.EOSE)).get().isPresent());
  }

  @Test
  void testMatchingOneOfMultipleReferencedPublicKeys() throws IOException, ExecutionException, InterruptedException {
    String referencedPubkeyMatch = "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
    String referencedPubkeyNoMatch = "0000000000000000000000000000000000000000000000000000000000000000";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReferencedPubKeyReqJson(referencedPubkeyMatch, referencedPubkeyNoMatch),
        referencedPubkeyMatch
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().isPresent());
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(referencedPubkeyMatch));

    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc"));
    assertTrue(Optional.of(returnedJsonMap.get(Command.EOSE)).get().isPresent());
  }

  private String createReferencedEventReqJson(@NonNull String uuid, String nonMatchingEventId) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    String eventIds = String.join("\",\"", uuid, nonMatchingEventId);
    return "[\"REQ\",\"" + uuid + "\",{\"#e\":[\"" + eventIds + "\"]}]";
  }

  private String createReferencedPubKeyReqJson(@NonNull String uuid, String nonMatchingEventId) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    String eventIds = String.join("\",\"", uuid, nonMatchingEventId);
    return "[\"REQ\",\"" + uuid + "\",{\"#p\":[\"" + eventIds + "\"]}]";
  }
}
