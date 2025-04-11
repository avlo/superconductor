package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.filter.Filters;
import nostr.event.filter.IdentifierTagFilter;
import nostr.event.message.ReqMessage;
import nostr.event.tag.IdentifierTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingIdentityTagIT {
  private final NostrRelayService nostrRelayService;
  private final static String D_TAG_VALUE_FROM_FILE = "matching_identity_single_filter_json_input-uuid";

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
  void testReqMessages() throws JsonProcessingException {
    String subscriberId = Factory.generateRandomHex64String();

    IdentifierTag identifierTag = new IdentifierTag();
    identifierTag.setUuid(D_TAG_VALUE_FROM_FILE);

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(new IdentifierTagFilter<>(identifierTag)));
    List<String> returnedEvents = nostrRelayService.sendRequestReturnEvents(reqMessage);
    log.debug("okMessage:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.contains(D_TAG_VALUE_FROM_FILE)));
  }
}
