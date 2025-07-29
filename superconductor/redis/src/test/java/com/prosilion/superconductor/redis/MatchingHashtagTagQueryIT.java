package com.prosilion.superconductor.redis;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.GenericTagQuery;
import com.prosilion.nostr.filter.tag.GenericTagQueryFilter;
import com.prosilion.nostr.filter.tag.HashtagTagFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.HashtagTag;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingHashtagTagQueryIT {
  private final NostrRelayServiceRedis nostrRelayService;
  private final String eventId = Factory.generateRandomHex64String();

  @Autowired
  MatchingHashtagTagQueryIT(@NonNull NostrRelayServiceRedis nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    assertTrue(nostrRelayService.send(
            (EventMessage) BaseMessageDecoder.decode(getEvent()))
        .getFlag());
  }

  @Test
  void testReqMessagesNoGenericMatch() throws IOException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String hashtagTagString = "textnote-hashtag-tag-2";
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(new HashtagTagFilter(
            new HashtagTag(hashtagTagString))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertEquals(1, returnedBaseMessages.size());
    List<EoseMessage> eoseMessageStream = returnedBaseMessages
        .stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast).toList();
    assertEquals(1, eoseMessageStream.size());
  }

  @Test
  void testReqMessagesMatchesGeneric() throws IOException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagString = "textnote-hashtag-tag-1";
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(new GenericTagQueryFilter(
            new GenericTagQuery("#t", genericTagString))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    //    associated event
    assertTrue(returnedEvents.stream().anyMatch(s -> s.getEventId().equals(
        eventId)));
    assertTrue(returnedEvents.stream().map(event ->
        event.getTags().stream().anyMatch(s -> s.toString().equals(genericTagString))).findAny().isPresent());
    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }

  @Test
  void testReqMessagesMatchesHashtagTag() throws IOException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String hashtagTagString = "textnote-hashtag-tag-1";
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(new HashtagTagFilter(
            new HashtagTag(hashtagTagString))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    //    associated event

    assertTrue(returnedEvents.stream().anyMatch(s -> s.getEventId().equals(eventId)));
    assertTrue(returnedEvents.stream().map(event ->
        event.getTags().stream().anyMatch(s -> s.toString().equals(hashtagTagString))).findAny().isPresent());
    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }

  private String getEvent() {
    return "[\n" +
        "  \"EVENT\",\n" +
        "  {\n" +
        "    \"content\": \"matching generic tag query filter test\",\n" +
        "    \"id\": \"" + eventId + "\",\n" +
        "    \"kind\": 1,\n" +
        "    \"created_at\": 1111111111111,\n" +
        "    \"pubkey\": \"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\",\n" +
        "    \"tags\": [\n" +
        "      [\n" +
        "        \"t\",\n" +
        "        \"textnote-hashtag-tag-1\"\n" +
        "      ]\n" +
        "    ],\n" +
        "    \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"\n" +
        "  }\n" +
        "]\n";
  }
}
