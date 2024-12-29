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
class SinceDateLessThanCreatedDateIT {
  private final NostrRelayService nostrRelayService;
  private final String textMessageEventJson;
  private final String uuidPrefix;

  @Autowired
  SinceDateLessThanCreatedDateIT(@NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix
  ) throws IOException {
    this.nostrRelayService = nostrRelayService;
    this.uuidPrefix = uuidPrefix;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/created_at_date_filter_json_input.txt"))) {
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
    sendReq();
  }

  private void sendReq() throws IOException, ExecutionException, InterruptedException {
    String uuid = "1111111111110";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(uuid),
        uuid
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    /**
     * "since" 1111111111110 should yield present, as target time (1111111111111) is after it
     */
    assertTrue(Optional.of(returnedJsonMap.get(Command.EVENT)).get().isPresent());
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("1111111111111"));

    assertTrue(Optional.of(returnedJsonMap.get(Command.EOSE)).get().isPresent());
  }

  private String createReqJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuidKey + "\",{\"authors\":[\"000d79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": " + uuid + "}]";
  }
}
