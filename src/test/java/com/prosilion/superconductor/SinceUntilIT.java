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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class SinceUntilIT {
  private final NostrRelayService nostrRelayService;
  private final String textMessageEventJson;
  private final String uuidPrefix;

  @Autowired
  SinceUntilIT(@NonNull NostrRelayService nostrRelayService,
               @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix
  ) throws IOException {
    this.nostrRelayService = nostrRelayService;
    this.uuidPrefix = uuidPrefix;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/created_at_date_filter_json_input.txt"))) {
      this.textMessageEventJson = lines.collect(Collectors.joining("\n"));
    }

    log.debug("setup() send event:\n  {}", textMessageEventJson);
    nostrRelayService.createEvent(textMessageEventJson);
    assertFalse(nostrRelayService.getEvents().isEmpty());
  }

  @Test
  void testReqCreatedDateAfterSinceUntilDatesMessages() throws IOException, ExecutionException, InterruptedException {
    sendCreatedDateAfterSinceUntilDatesReq(String.valueOf(0));
  }

  private void sendCreatedDateAfterSinceUntilDatesReq(String increment) throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqCreatedDateAfterSinceUntilDatesJson(increment),
        increment
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    /**
     * since 1111111111112 and until 1111111111113 should yield empty, since target time (1111111111111) is before the two
     */
    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqCreatedDateAfterSinceUntilDatesJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111112,\"until\": 1111111111113}]";
  }

  @Test
  void testReqCreatedDateBeforeSinceUntilDatesMessages() throws IOException, ExecutionException, InterruptedException {
    sendCreatedDateBeforeSinceUntilDatesReq(String.valueOf(0));
  }

  private void sendCreatedDateBeforeSinceUntilDatesReq(String increment) throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqCreatedDateBeforeSinceUntilDatesJson(increment),
        increment
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    /**
     * since 1111111111109 and until 1111111111110 should yield empty, since target time (1111111111111) is not between the two
     */
    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());

    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqCreatedDateBeforeSinceUntilDatesJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111109,\"until\": 1111111111110}]";
  }

  @Test
  void testReqCreatedDateBetweenSinceUntilDatesMessages() throws IOException, ExecutionException, InterruptedException {
    sendCreatedDateBetweenSinceUntilDatesReq(String.valueOf(0));
  }

  private void sendCreatedDateBetweenSinceUntilDatesReq(String increment) throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqCreatedDateBetweenSinceUntilDatesJson(increment),
        increment
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    /**
     + "since" 1111111111110 and until 1111111111112 should yield present, as target time (1111111111111) is between the two
     */
    assertTrue(returnedJsonMap.get(Command.EVENT).isPresent());
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("1111111111111"));

//    associated event
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("aaabbb6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc"));

    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqCreatedDateBetweenSinceUntilDatesJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111110,\"until\": 1111111111112}]";
  }

  @Test
  void testReqUntilDateGreaterThanCreatedDateMessages() throws IOException, ExecutionException, InterruptedException {
    sendUntilDateGreaterThanCreatedDateReq();
  }

  private void sendUntilDateGreaterThanCreatedDateReq() throws IOException, ExecutionException, InterruptedException {
    String uuid = "1111111111112";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqUntilDateGreaterThanCreatedDateJson(uuid),
        uuid
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    /**
     * "until" 1111111111112 should yield present, as target time (1111111111111) is before it
     */
    assertTrue(returnedJsonMap.get(Command.EVENT).isPresent());
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("1111111111111"));

    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqUntilDateGreaterThanCreatedDateJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"until\": " + uuid + "}]";
  }

  @Test
  void testReqUntilDateGreaterThanCreatedDatePubKeyTagMessages() throws IOException, ExecutionException, InterruptedException {
    sendUntilDateGreaterThanCreatedDatePubKeyTagReq();
  }

  private void sendUntilDateGreaterThanCreatedDatePubKeyTagReq() throws IOException, ExecutionException, InterruptedException {
    String uuid = "1111111111112";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqUntilDateGreaterThanCreatedDatePubKeyTagJson(uuid),
        uuid
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    /**
     * "until" 1111111111112 should yield present, as target time (1111111111111) is before it
     */
    assertTrue(returnedJsonMap.get(Command.EVENT).isPresent());
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("1111111111111"));

    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqUntilDateGreaterThanCreatedDatePubKeyTagJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"until\": " + uuid + "}]";
  }

  @Test
  void testReqUntilDateLessThanCreatedDateMessages() throws IOException, ExecutionException, InterruptedException {
    sendUntilDateLessThanCreatedDateReq(String.valueOf(0));
  }

  private void sendUntilDateLessThanCreatedDateReq(String increment) throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqUntilDateLessThanCreatedDateJson(increment),
        increment
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    /**
     * until 1111111111110 should yield empty, since target time (1111111111111) is after it
     */
    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqUntilDateLessThanCreatedDateJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"until\": 1111111111110}]";
  }

  @Test
  void testReqSinceDateGreaterThanCreatedDateMessages() throws IOException, ExecutionException, InterruptedException {
    sendSinceDateGreaterThanCreatedDateReq(String.valueOf(0));
  }

  private void sendSinceDateGreaterThanCreatedDateReq(String increment) throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(increment),
        increment
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    /**
     * since 1111111111112 should yield empty, since target time (1111111111111) is before it
     */
    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111112}]";
  }


  @Test
  void testReqSinceDateLessThanCreatedDateMessages() throws IOException, ExecutionException, InterruptedException {
    sendSinceDateLessThanCreatedDateReq();
  }

  private void sendSinceDateLessThanCreatedDateReq() throws IOException, ExecutionException, InterruptedException {
    String uuid = "1111111111110";
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqSinceDateLessThanCreatedDateJson(uuid),
        uuid
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);

    /**
     * "since" 1111111111110 should yield present, as target time (1111111111111) is after it
     */
    assertTrue(returnedJsonMap.get(Command.EVENT).isPresent());
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains("1111111111111"));

    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqSinceDateLessThanCreatedDateJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": " + uuid + "}]";
  }
}
