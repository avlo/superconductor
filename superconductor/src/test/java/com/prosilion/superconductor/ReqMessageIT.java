package com.prosilion.superconductor;

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
@ActiveProfiles("test")
class ReqMessageIT {
  private final NostrRelayService nostrRelayService;

  @Autowired
  ReqMessageIT(@NonNull NostrRelayService nostrRelayService) {
    this.nostrRelayService = nostrRelayService;
  }

  @Test
  void testReqFilteredByEventAndAuthor() {
    String uuidKey = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
    String authorPubkey = "dddeeef81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(uuidKey, authorPubkey),
        uuidKey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(uuidKey)));
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(authorPubkey)));
  }

  private String createReqJson(@NonNull String uuid, @NonNull String authorPubkey) {
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"],\"authors\":[\"" + authorPubkey + "\"]}]";
  }

  @Test
  void testReqFilteredByEventId() {
    String uuidKey = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";

    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createEventReqJson(uuidKey),
        uuidKey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(uuidKey)));
  }

  private String createEventReqJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
  }

  @Test
  void testReqFilteredByAuthor() {
    String authorPubkey = "dddeeef81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
    String subscriberId = Factory.generateRandomHex64String();

    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createAuthorReqJson(subscriberId, authorPubkey),
        authorPubkey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(authorPubkey)));

    //    additional eventId confirmation
    String eventId = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains(eventId)));
  }

  private String createAuthorReqJson(@NonNull String subscriberId, @NonNull String authorPubkey) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"" + authorPubkey + "\"]}]";
  }
}
