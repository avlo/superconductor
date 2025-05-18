package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.api.NIP01;
import nostr.event.BaseMessage;
import nostr.event.filter.Filters;
import nostr.event.filter.HashtagTagFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EoseMessage;
import nostr.event.message.EventMessage;
import nostr.event.message.ReqMessage;
import nostr.event.tag.HashtagTag;
import nostr.id.Identity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.EventMessageIT.getGenericEvents;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingGenericTagSingleLetterQueryIT {
  private final NostrRelayService nostrRelayService;

  Identity identity = Factory.createNewIdentity();
  String content = Factory.lorumIpsum(getClass());

  @Autowired
  MatchingGenericTagSingleLetterQueryIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    NIP01 textNoteEvent = new NIP01(identity).createTextNoteEvent(content);
    textNoteEvent.addTag(new HashtagTag("h-tag-1"));
    assertTrue(
        nostrRelayService
            .send(
                new EventMessage(textNoteEvent.sign().getEvent()))
            .getFlag());
  }

  @Test
  void testReqMessagesNoGenericMatch() throws JsonProcessingException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String hashTagString = "textnote-geo-tag-2";

    List<BaseMessage> baseMessages = nostrRelayService.send(
        new ReqMessage(subscriberId, new Filters(
        new HashtagTagFilter<>(new HashtagTag(hashTagString)))));
    assertEquals(1, baseMessages.size());
    assertTrue(baseMessages
        .stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
  }

  @Test
  void testReqMessagesMatchesGeneric() throws JsonProcessingException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String hashTagString = "h-tag-1";

    List<BaseMessage> returnedBaseMessages = nostrRelayService
        .send(
            new ReqMessage(subscriberId, new Filters(
                new HashtagTagFilter<>(new HashtagTag(hashTagString)))));

    List<GenericEvent> events = getGenericEvents(returnedBaseMessages);
    
    assertFalse(events.isEmpty());
    //    associated event
    assertTrue(events.stream().anyMatch(s -> s.getPubKey().toHexString().equals(identity.getPublicKey().toHexString())));
    assertTrue(events.stream().anyMatch(s -> s.getTags().stream().anyMatch(tag -> tag.toString().contains(hashTagString))));
  }
}
