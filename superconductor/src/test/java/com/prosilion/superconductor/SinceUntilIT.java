package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.EventMessageIT.getGenericEventKindIFs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class SinceUntilIT {
  private final NostrRelayService nostrRelayService;

  @Autowired
  SinceUntilIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/created_at_date_filter_json_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      assertTrue(
          nostrRelayService.send(
                  (EventMessage) BaseMessageDecoder.decode(textMessageEventJson))
              .getFlag());
    }
  }

  @Test
  void testReqCreatedDateAfterSinceUntilDatesMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqCreatedDateAfterSinceUntilDatesJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);
    
    /*
      since 1111111111112 and until 1111111111113 should yield empty, since target time (1111111111111) is before the two
     */
    assertTrue(returnedEvents.isEmpty());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
  }

  private String createReqCreatedDateAfterSinceUntilDatesJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111112,\"until\": 1111111111113}]";
  }

  @Test
  void testReqCreatedDateBeforeSinceUntilDatesMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqCreatedDateBeforeSinceUntilDatesJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    /*
     * since 1111111111109 and until 1111111111110 should yield empty, since target time (1111111111111) is not between the two
     */
    assertTrue(returnedEvents.isEmpty());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
  }

  private String createReqCreatedDateBeforeSinceUntilDatesJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111109,\"until\": 1111111111110}]";
  }

  @Test
  void testReqCreatedDateBetweenSinceUntilDatesMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqCreatedDateBetweenSinceUntilDatesJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
    
    /*
     + "since" 1111111111110 and until 1111111111112 should yield present, as target time (1111111111111) is between the two
     */
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getCreatedAt().equals(1111111111111L)));

//    associated event
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals("aaabbb6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc")));
//    TODO: investigate below EOSE missing, causes test failure
//    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqCreatedDateBetweenSinceUntilDatesJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111110,\"until\": 1111111111112}]";
  }

  @Test
  void testReqUntilDateGreaterThanCreatedDateMessages() throws JsonProcessingException, NostrException {
    String until = "1111111111112";
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqUntilDateGreaterThanCreatedDateJson(subscriberId, until));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());

    /*
     * "until" 1111111111112 should yield present, as target time (1111111111111) is before it
     */
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getCreatedAt().equals(1111111111111L)));
  }

  private String createReqUntilDateGreaterThanCreatedDateJson(String subscriberId, @NonNull String until) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"until\": " + until + "}]";
  }

  @Test
  void testReqUntilDateGreaterThanCreatedDatePubKeyTagMessages() throws JsonProcessingException, NostrException {
    String uuid = "1111111111112";
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqUntilDateGreaterThanCreatedDatePubKeyTagJson(subscriberId, uuid));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());

    /*
     * "until" 1111111111112 should yield present, as target time (1111111111111) is before it
     */
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getCreatedAt().equals(1111111111111L)));
  }

  private String createReqUntilDateGreaterThanCreatedDatePubKeyTagJson(String subscriberId, @NonNull String until) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"until\": " + until + "}]";
  }

  @Test
  void testReqUntilDateLessThanCreatedDateMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqUntilDateLessThanCreatedDateJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    /*
     * until 1111111111110 should yield empty, since target time (1111111111111) is after it
     */
    assertTrue(returnedEvents.isEmpty());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
  }

  private String createReqUntilDateLessThanCreatedDateJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"until\": 1111111111110}]";
  }

  @Test
  void testReqSinceDateGreaterThanCreatedDateMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqSinceDateGreaterThanCreatedDateJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    /*
     * since 1111111111112 should yield empty, since target time (1111111111111) is before it
     */
    assertTrue(returnedEvents.isEmpty());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
  }

  private String createReqSinceDateGreaterThanCreatedDateJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": 1111111111112}]";
  }

  @Test
  void testReqSinceDateLessThanCreatedDateMessages() throws JsonProcessingException, NostrException {
    String since = "1111111111110";
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqSinceDateLessThanCreatedDateJson(subscriberId, since));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    /*
     * "since" 1111111111110 should yield present, as target time (1111111111111) is after it
     */
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getCreatedAt().equals(1111111111111L)));
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
  }

  private String createReqSinceDateLessThanCreatedDateJson(String subscriberId, @NonNull String since) {
    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"aaabbbf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"],\"since\": " + since + "}]";
  }
}
