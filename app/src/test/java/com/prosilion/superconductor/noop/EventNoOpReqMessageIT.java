package com.prosilion.superconductor.noop;

import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = {"/event.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS) // class level @Sql
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("testnoop")
class EventNoOpReqMessageIT {
  private final NostrRelayService nostrRelayService;

  private final String eventIdFromEventSql = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  private final String authorPubkeyFromEventSql = "dddeeef81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

  @Autowired
  EventNoOpReqMessageIT(@NonNull NostrRelayService nostrRelayService) {
    this.nostrRelayService = nostrRelayService;
  }

  @Test
  void testReqFilteredByEventAndAuthor() {
    final String subscriberId = Factory.generateRandomHex64String();
    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(subscriberId, eventIdFromEventSql, authorPubkeyFromEventSql),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(eventIdFromEventSql)));
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(authorPubkeyFromEventSql)));
  }

  private String createReqJson(@NonNull String uuid, @NonNull String eventId, @NonNull String authorPubkey) {
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + eventId + "\"],\"authors\":[\"" + authorPubkey + "\"]}]";
  }

  @Test
  void testReqFilteredByEventId() {
    final String subscriberId = Factory.generateRandomHex64String();
    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createEventReqJson(subscriberId, eventIdFromEventSql),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(eventIdFromEventSql)));
  }

  private String createEventReqJson(@NonNull String subscriberId, @NonNull String eventId) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"ids\":[\"" + eventId + "\"]}]";
  }

  @Test
  void testReqFilteredByAuthor() {
    final String subscriberId = Factory.generateRandomHex64String();
    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createAuthorReqJson(subscriberId, authorPubkeyFromEventSql),
        subscriberId
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(authorPubkeyFromEventSql)));
//    additional eventId confirmation
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(eventIdFromEventSql)));
  }

  private String createAuthorReqJson(@NonNull String subscriberId, @NonNull String authorPubkey) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"" + authorPubkey + "\"]}]";
  }
}
