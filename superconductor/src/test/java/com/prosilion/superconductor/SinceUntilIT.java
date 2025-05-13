//package com.prosilion.superconductor;
//
//import com.prosilion.superconductor.util.Factory;
//import com.prosilion.superconductor.util.NostrRelayService;
//import java.util.List;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import nostr.base.Command;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Slf4j
//@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
//class SinceUntilIT {
//  private final NostrRelayService nostrRelayService;
//
//  @Autowired
//  SinceUntilIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
//    this.nostrRelayService = nostrRelayService;
//
//    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/created_at_date_filter_json_input.txt"))) {
//      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
//      log.debug("setup() send event:\n  {}", textMessageEventJson);
//      assertTrue(nostrRelayService.send(textMessageEventJson).getFlag());
//    }
//  }
//
//  @Test
//  void testReqCreatedDateAfterSinceUntilDatesMessages() {
//    String subscriberId = Factory.generateRandomHex64String();
//    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
//        createReqCreatedDateAfterSinceUntilDatesJson(subscriberId),
//        subscriberId
//    );
//    log.debug("okMessage:");
//    log.debug("  " + returnedJsonMap);
//
//    /*
//      since 1111111111112 and until 1111111111113 should yield empty, since target time (1111111111111) is before the two
//     */
//    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
//    assertFalse(returnedJsonMap.get(Command.EOSE).isEmpty());
//  }
//
//  private String createReqCreatedDateAfterSinceUntilDatesJson(@NonNull String uuid) {
//    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111112,\"until\": 1111111111113}]";
//  }
//
//  @Test
//  void testReqCreatedDateBeforeSinceUntilDatesMessages() {
//    String subscriberId = Factory.generateRandomHex64String();
//    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
//        createReqCreatedDateBeforeSinceUntilDatesJson(subscriberId),
//        subscriberId
//    );
//    log.debug("okMessage:");
//    log.debug("  " + returnedJsonMap);
//
//    /*
//     * since 1111111111109 and until 1111111111110 should yield empty, since target time (1111111111111) is not between the two
//     */
//    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
//    assertFalse(returnedJsonMap.get(Command.EOSE).isEmpty());
//  }
//
//  private String createReqCreatedDateBeforeSinceUntilDatesJson(@NonNull String uuid) {
//    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111109,\"until\": 1111111111110}]";
//  }
//
//  @Test
//  void testReqCreatedDateBetweenSinceUntilDatesMessages() {
//    String subscriberId = Factory.generateRandomHex64String();
//    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
//        createReqCreatedDateBetweenSinceUntilDatesJson(subscriberId),
//        subscriberId
//    );
//    log.debug("okMessage:");
//    log.debug("  " + returnedJsonMap);
//
//    /*
//     + "since" 1111111111110 and until 1111111111112 should yield present, as target time (1111111111111) is between the two
//     */
//    assertFalse(returnedJsonMap.get(Command.EVENT).isEmpty());
//    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains("1111111111111")));
//
////    associated event
//    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains("aaabbb6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc")));
////    TODO: investigate below EOSE missing, causes test failure
////    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
//  }
//
//  private String createReqCreatedDateBetweenSinceUntilDatesJson(@NonNull String uuid) {
//    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111110,\"until\": 1111111111112}]";
//  }
//
//  @Test
//  void testReqUntilDateGreaterThanCreatedDateMessages() {
//    String until = "1111111111112";
//    String subscriberId = Factory.generateRandomHex64String();
//    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
//        createReqUntilDateGreaterThanCreatedDateJson(subscriberId, until),
//        subscriberId
//    );
//    log.debug("okMessage:");
//    log.debug("  " + returnedJsonMap);
//
//    /*
//     * "until" 1111111111112 should yield present, as target time (1111111111111) is before it
//     */
//    assertFalse(returnedJsonMap.get(Command.EVENT).isEmpty());
//    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains("1111111111111")));
//    assertFalse(returnedJsonMap.get(Command.EOSE).isEmpty());
//  }
//
//  private String createReqUntilDateGreaterThanCreatedDateJson(String subscriberId, @NonNull String until) {
//    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"until\": " + until + "}]";
//  }
//
//  @Test
//  void testReqUntilDateGreaterThanCreatedDatePubKeyTagMessages() {
//    String uuid = "1111111111112";
//    String subscriberId = Factory.generateRandomHex64String();
//    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
//        createReqUntilDateGreaterThanCreatedDatePubKeyTagJson(subscriberId, uuid),
//        subscriberId
//    );
//    log.debug("okMessage:");
//    log.debug("  " + returnedJsonMap);
//
//    /*
//     * "until" 1111111111112 should yield present, as target time (1111111111111) is before it
//     */
//    assertFalse(returnedJsonMap.get(Command.EVENT).isEmpty());
//    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains("1111111111111")));
//    assertFalse(returnedJsonMap.get(Command.EOSE).isEmpty());
//  }
//
//  private String createReqUntilDateGreaterThanCreatedDatePubKeyTagJson(String subscriberId, @NonNull String until) {
//    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"until\": " + until + "}]";
//  }
//
//  @Test
//  void testReqUntilDateLessThanCreatedDateMessages() {
//    String subscriberId = Factory.generateRandomHex64String();
//    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
//        createReqUntilDateLessThanCreatedDateJson(subscriberId),
//        subscriberId
//    );
//    log.debug("okMessage:");
//    log.debug("  " + returnedJsonMap);
//
//    /*
//     * until 1111111111110 should yield empty, since target time (1111111111111) is after it
//     */
//
//    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
//    assertFalse(returnedJsonMap.get(Command.EOSE).isEmpty());
//  }
//
//  private String createReqUntilDateLessThanCreatedDateJson(@NonNull String uuid) {
//    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"until\": 1111111111110}]";
//  }
//
//  @Test
//  void testReqSinceDateGreaterThanCreatedDateMessages() {
//    String subscriberId = Factory.generateRandomHex64String();
//    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
//        createReqSinceDateGreaterThanCreatedDateJson(subscriberId),
//        subscriberId
//    );
//    log.debug("okMessage:");
//    log.debug("  " + returnedJsonMap);
//
//    /*
//     * since 1111111111112 should yield empty, since target time (1111111111111) is before it
//     */
//    assertTrue(returnedJsonMap.get(Command.EVENT).isEmpty());
//    assertFalse(returnedJsonMap.get(Command.EOSE).isEmpty());
//  }
//
//  private String createReqSinceDateGreaterThanCreatedDateJson(@NonNull String uuid) {
//    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111112}]";
//  }
//
//  @Test
//  void testReqSinceDateLessThanCreatedDateMessages() {
//    String since = "1111111111110";
//    String subscriberId = Factory.generateRandomHex64String();
//    Map<Command, List<String>> returnedJsonMap = nostrRelayService.sendRequest(
//        createReqSinceDateLessThanCreatedDateJson(subscriberId, since),
//        subscriberId
//    );
//    log.debug("okMessage:");
//    log.debug("  " + returnedJsonMap);
//
//    /*
//     * "since" 1111111111110 should yield present, as target time (1111111111111) is after it
//     */
//    assertTrue(returnedJsonMap.get(Command.EVENT).stream().anyMatch(s -> s.contains("1111111111111")));
//    assertFalse(returnedJsonMap.get(Command.EOSE).isEmpty());
//  }
//
//  private String createReqSinceDateLessThanCreatedDateJson(String subscriberId, @NonNull String since) {
//    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": " + since + "}]";
//  }
//}
