package com.prosilion.superconductor.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.redis.util.Factory;
import com.prosilion.superconductor.redis.util.NostrRelayServiceRedis;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.redis.TextNoteEventMessageRedisIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class SinceUntilIT {
  private final NostrRelayServiceRedis nostrRelayService;
  private final String eventId = Factory.generateRandomHex64String();
  private final String publicKey = Factory.generateRandomHex64String();

  @Autowired
  SinceUntilIT(@NonNull NostrRelayServiceRedis nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    assertTrue(
        nostrRelayService.send(
                (EventMessage) BaseMessageDecoder.decode(getEvent()))
            .getFlag());
  }

  @Test
  void testReqCreatedDateAfterSinceUntilDatesMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqCreatedDateAfterSinceUntilDatesJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);
    
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
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"" + publicKey + "\"],\"since\": 1111111111112,\"until\": 1111111111113}]";
  }

  @Test
  void testReqCreatedDateBeforeSinceUntilDatesMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqCreatedDateBeforeSinceUntilDatesJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

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
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"" + publicKey + "\"],\"since\": 1111111111109,\"until\": 1111111111110}]";
  }

  @Test
  void testReqCreatedDateBetweenSinceUntilDatesMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqCreatedDateBetweenSinceUntilDatesJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

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
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
//    TODO: investigate below EOSE missing, causes test failure
//    assertTrue(returnedJsonMap.get(Command.EOSE).isPresent());
  }

  private String createReqCreatedDateBetweenSinceUntilDatesJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"" + publicKey + "\"],\"since\": 1111111111110,\"until\": 1111111111112}]";
  }

  @Test
  void testReqUntilDateGreaterThanCreatedDateMessages() throws JsonProcessingException, NostrException {
    String until = "1111111111112";
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqUntilDateGreaterThanCreatedDateJson(subscriberId, until));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

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
    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"" + publicKey + "\"],\"until\": " + until + "}]";
  }

  @Test
  void testReqUntilDateGreaterThanCreatedDatePubKeyTagMessages() throws JsonProcessingException, NostrException {
    String uuid = "1111111111112";
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqUntilDateGreaterThanCreatedDatePubKeyTagJson(subscriberId, uuid));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

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
    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"" + publicKey + "\"],\"until\": " + until + "}]";
  }

  @Test
  void testReqUntilDateLessThanCreatedDateMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqUntilDateLessThanCreatedDateJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

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
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"" + publicKey + "\"],\"until\": 1111111111110}]";
  }

  @Test
  void testReqSinceDateGreaterThanCreatedDateMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqSinceDateGreaterThanCreatedDateJson(subscriberId));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

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
    return "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"" + publicKey + "\"],\"since\": 1111111111112}]";
  }

  @Test
  void testReqSinceDateLessThanCreatedDateMessages() throws JsonProcessingException, NostrException {
    String since = "1111111111110";
    String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = ReqMessage.decode(subscriberId, createReqSinceDateLessThanCreatedDateJson(subscriberId, since));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

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
    return "[\"REQ\",\"" + subscriberId + "\",{\"authors\":[\"" + publicKey + "\"],\"since\": " + since + "}]";
  }

  private String getEvent() {
    return "[\n" +
        "  \"EVENT\",\n" +
        "  {\n" +
        "    \"content\": \"created at test\",\n" +
        "    \"id\":\"" + eventId + "\",\n" +
        "    \"kind\": 1,\n" +
        "    \"created_at\": 1111111111111,\n" +
        "    \"pubkey\": \"" + publicKey + "\",\n" +
        "    \"tags\": [\n" +
        "      [\n" +
        "        \"a\",\n" +
        "        \"30023:aaabbbd4c1394dda46d09f35bd384dd30cc552ad5541990f98844fb06676e9ca:abcd\",\n" +
        "        \"wss://nostr.example.com\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"custom-tag\",\n" +
        "        \"created at date custom-tag random value\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"p\",\n" +
        "        \"bbbcccf81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"e\",\n" +
        "        \"aaabbbac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"g\",\n" +
        "        \"created at date textnote geo-tag-1\"\n" +
        "      ]\n" +
        "    ],\n" +
        "    \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"\n" +
        "  }\n" +
        "]\n";
  }
}
