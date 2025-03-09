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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class EventMessageIT {
  private final NostrRelayService nostrRelayService;

  private final String authorPubKey;
  private final String eventId;

  @Autowired
  EventMessageIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    eventId = Factory.generateRandomHex64String();
    authorPubKey = Factory.generateRandomHex64String();
    String content = Factory.lorumIpsum(getClass());

    String globalEventJson = "[\"EVENT\",{\"id\":\"" + eventId + "\",\"kind\":1,\"content\":\"" + content + "\",\"pubkey\":\"" + authorPubKey + "\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
    log.debug("setup() send event:\n  {}", globalEventJson);

    nostrRelayService.createEvent(globalEventJson);
    assertFalse(nostrRelayService.getEvents().isEmpty());
  }

  @Test
  void testReqFilteredByEventAndAuthor() throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(eventId, authorPubKey),
        Factory.generateRandomHex64String() // client UUID
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).isPresent());
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains(eventId));
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains(authorPubKey));
  }

  private String createReqJson(@NonNull String uuid, @NonNull String authorPubkey) {
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"],\"authors\":[\"" + authorPubkey + "\"]}]";
  }

  @Test
  void testReqFilteredByEventId() throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createEventReqJson(eventId),
        Factory.generateRandomHex64String() // client UUID
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains(eventId));
  }

  private String createEventReqJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
  }

  @Test
  void testReqFilteredByAuthor() throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createAuthorReqJson(authorPubKey),
        Factory.generateRandomHex64String() // client UUID
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains(authorPubKey));
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow().contains(eventId));
  }

  private String createAuthorReqJson(@NonNull String authorPubkey) {
    return "[\"REQ\",\"" + authorPubkey + "\",{\"authors\":[\"" + authorPubkey + "\"]}]";
  }

  @Test
  void testReqNonMatchingEvent() throws IOException, ExecutionException, InterruptedException {
    String nonMatchingsubscriberId = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
    String nonMatchingEventId = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createNonMatchEventReqJson(nonMatchingsubscriberId, nonMatchingEventId),
        nonMatchingsubscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createNonMatchEventReqJson(@NonNull String subscriberId, @NonNull String nonMatchingEventId) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"ids\":[\"" + nonMatchingEventId + "\"]}]";
  }
}
