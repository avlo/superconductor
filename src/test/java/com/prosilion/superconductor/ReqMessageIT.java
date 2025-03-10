package com.prosilion.superconductor;

import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = {"/event.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS) // class level @Sql
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class ReqMessageIT {
  private final NostrRelayService nostrRelayService;
  private final String uuidPrefix;

  @Autowired
  ReqMessageIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix
  ) {
    this.nostrRelayService = nostrRelayService;
    this.uuidPrefix = uuidPrefix;
  }

  @Test
  void testReqFilteredByEventAndAuthor() throws IOException, ExecutionException, InterruptedException {
    String uuidKey = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
    String authorPubkey = "dddeeef81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(uuidKey, authorPubkey),
        uuidKey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(uuidKey));
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(authorPubkey));
  }

  private String createReqJson(@NonNull String uuid, @NonNull String authorPubkey) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"],\"authors\":[\"" + authorPubkey + "\"]}]";
  }

  @Test
  void testReqFilteredByEventId() throws IOException, ExecutionException, InterruptedException {
    String uuidKey = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";

    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createEventReqJson(uuidKey),
        uuidKey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(uuidKey));
  }

  private String createEventReqJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
  }

  @Test
  void testReqFilteredByAuthor() throws IOException, ExecutionException, InterruptedException {
    String authorPubkey = "dddeeef81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createAuthorReqJson(authorPubkey),
        authorPubkey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(authorPubkey));

//    additional eventId confirmation
    String eventId = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(eventId));
  }

  private String createAuthorReqJson(@NonNull String authorPubkey) {
    final String uuidKey = Strings.concat(uuidPrefix, authorPubkey);
    return "[\"REQ\",\"" + uuidPrefix + "\",{\"authors\":[\"" + authorPubkey + "\"]}]";
  }
}
