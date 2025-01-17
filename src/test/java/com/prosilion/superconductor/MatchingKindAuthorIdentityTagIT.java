package com.prosilion.superconductor;

import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import nostr.event.Kind;
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
import java.util.List;
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
class MatchingKindAuthorIdentityTagIT {
  private final NostrRelayService nostrRelayService;
  private final String textMessageEventJson;

  @Autowired
  MatchingKindAuthorIdentityTagIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_kind_author_identitytag_filter_input.json"))) {
      this.textMessageEventJson = lines.collect(Collectors.joining("\n"));
    }
  }

  @BeforeAll
  public void setup() throws IOException {
    log.debug("setup() send event:\n  {}", textMessageEventJson);
    nostrRelayService.createEvent(textMessageEventJson);
    assertTrue(!List.of(nostrRelayService.getEvents()).isEmpty());
  }

  @Test
  void testReqMessages() throws IOException, ExecutionException, InterruptedException {
    final String targetEventId = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
    final String uuid = "superconductor_subscriber_id-0";
    final String authorPubKey = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createClEventJsonReq(uuid, authorPubKey),
        uuid);
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().isPresent());

//    associated event
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc"));
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("matching kind, author, identity-tag filter test"));
    assertTrue(Optional.of(returnedJsonMap.get(Command.EOSE)).get().isPresent());
  }

  private String createClEventJsonReq(String uuid, String authorPubKey) {
    String reqJson = "[\"REQ\",\"" + uuid +
        "\",{" +
        "\"kinds\":[\"" + Kind.CALENDAR_TIME_BASED_EVENT + "\"]," +
        "\"authors\":[\"" + authorPubKey + "\"]," +
        "\"#d\":[\"" + uuid + "\"]" +
        "}]";
    return reqJson;
  }
}
