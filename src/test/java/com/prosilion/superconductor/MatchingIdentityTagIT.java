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

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("test")
class MatchingIdentityTagIT {
  private final NostrRelayService nostrRelayService;
  private final String textMessageEventJson;
  private final String uuidPrefix;
  private final Integer targetCount;

  @Autowired
  MatchingIdentityTagIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix
  ) throws IOException {
    this.nostrRelayService = nostrRelayService;
    this.uuidPrefix = uuidPrefix;
    this.targetCount = 1;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_identity_single_filter_json_input.txt"))) {
      this.textMessageEventJson = lines.collect(Collectors.joining("\n"));
    }
  }

  @BeforeAll
  public void setup() throws IOException {
    nostrRelayService.createEvent(textMessageEventJson);
  }

  @Test
  void testReqMessages() throws IOException, ExecutionException, InterruptedException {
//    IntStream.range(0, targetCount).parallel().forEach(increment -> {
    sendReq(String.valueOf(0));
//    });
  }

  private void sendReq(String increment) throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(increment),
        increment
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    String dTag = "[\"d\",\"" + uuidPrefix + increment + "\"]";
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(dTag));
  }

  private String createReqJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuidKey + "\",{\"#d\":[\"" + uuidKey + "\"]}]";
  }
}
