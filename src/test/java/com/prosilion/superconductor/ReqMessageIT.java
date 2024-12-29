package com.prosilion.superconductor;

import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("test")
class ReqMessageIT {
  public final static String globalEventJson = "[\"EVENT\",{\"id\":\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\",\"kind\":1,\"content\":\"1111111111\",\"pubkey\":\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";

  private final NostrRelayService nostrRelayService;
  private final String uuidPrefix;

  @Autowired
  ReqMessageIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix
  ) throws IOException {
    this.nostrRelayService = nostrRelayService;
    this.uuidPrefix = uuidPrefix;
  }

  @Test
  void testEventMessageThenReqMessage() throws IOException, ExecutionException, InterruptedException {
    String uuidKey = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
    String authorPubkey = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(uuidKey, authorPubkey),
        uuidKey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
//    String dTag = "[\"d\",\"" + uuidPrefix + increment + "\"]";
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(uuidKey));
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(authorPubkey));
  }

  private String createReqJson(@NonNull String uuid, @NonNull String authorPubkey) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuidKey + "\",{\"ids\":[\"" + uuid + "\"],\"authors\":[\"" + authorPubkey + "\"]}]";
  }
}
