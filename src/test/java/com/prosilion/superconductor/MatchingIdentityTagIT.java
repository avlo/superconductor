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
class MatchingIdentityTagIT {
  private final NostrRelayService nostrRelayService;
  private final static String SUBSCRIBER_ID = Factory.generateRandomHex64String();

  @Autowired
  MatchingIdentityTagIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_identity_single_filter_json_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      nostrRelayService.createEvent(textMessageEventJson);
      assertFalse(nostrRelayService.getEvents().isEmpty());
    }
  }

  @Test
  void testReqMessages() throws IOException, ExecutionException, InterruptedException {
    String reqJson = createReqJson("some-uuid");
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        reqJson,
        SUBSCRIBER_ID
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).orElseThrow().contains("some-uuid"));
  }

  @Test
  void testAddressableReqMessages()  {
//    TODO: request addressable filter isn't intended to w/ simple string match, revisit when time
//    String reqJson = createAddressableReqJson(SUBSCRIBER_ID, "30023:f7234bd4c1394dda46d09f35bd384dd30cc552ad5541990f98844fb06676e9ca:abcd");
//    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
//        reqJson,
//        SUBSCRIBER_ID
//    );
//    log.debug("okMessage:");
//    log.debug("  " + returnedJsonMap);
//    assertTrue(returnedJsonMap.get(Command.EVENT).orElseThrow().contains(SUBSCRIBER_ID));
  }

  private String createReqJson(String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"#d\":[\"" + uuid + "\"]}]";
  }

  private String createAddressableReqJson(String uuid, String target) {
    return "[\"REQ\",\"" + uuid + "\",{\"#a\":[\"" + target + "\"]}]";
  }
}
