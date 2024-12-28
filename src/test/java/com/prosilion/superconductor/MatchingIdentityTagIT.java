package com.prosilion.superconductor;

import com.prosilion.superconductor.util.NostrRelayService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("test")
class MatchingIdentityTagIT {
  private static final String TARGET_TEXT_MESSAGE_EVENT_CONTENT = "superconductor_subscriber_id-";

  public final String textMessageEventJson;
  private final Integer targetCount;

  private final NostrRelayService nostrRelayService;

  @Autowired
  MatchingIdentityTagIT(NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    this.targetCount = 1;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_identity_single_filter_json_input.txt"))) {
      this.textMessageEventJson = lines.collect(Collectors.joining("\n"));
    }
  }

  @BeforeAll
  public void setup() {
    IntStream.range(0, targetCount).parallel().forEach(increment -> {
      try {
        createEvent(increment);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private void createEvent(int increment) throws IOException {
    nostrRelayService.createEvent(textMessageEventJson);
  }

  @Test
  void testReqMessages() throws IOException, ExecutionException, InterruptedException {
//    IntStream.range(0, targetCount).parallel().forEach(increment -> {
    sendReq(0);
//    });
  }

  private void sendReq(int increment) throws IOException, ExecutionException, InterruptedException {
    String s = nostrRelayService.sendRequest(String.valueOf(increment));
    assertTrue(s.contains(TARGET_TEXT_MESSAGE_EVENT_CONTENT + increment));
  }
}
