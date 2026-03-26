package com.prosilion.superconductor.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.HashtagTagFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.HashtagTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.util.Factory;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseMatchingGenericTagSingleLetterQueryIT {
  Identity identity = Factory.createNewIdentity();
  String content = Factory.lorumIpsum(getClass());
  private final String relayUrl;

  public BaseMatchingGenericTagSingleLetterQueryIT(@NonNull String relayUrl) throws IOException {
    NostrEventPublisher nostrEventPublisher = new NostrEventPublisher(relayUrl);
    this.relayUrl = relayUrl;
    assertTrue(
        nostrEventPublisher
            .send(
                new EventMessage(
                    new TextNoteEvent(
                        identity,
                        List.of(new HashtagTag("h-tag-1")),
                        content)))
            .getFlag());
  }

  @Test
  void testReqMessagesNoGenericMatch() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String hashTagString = "textnote-geo-tag-2";

    List<BaseMessage> baseMessages = new NostrSingleRequestService().send(
        new ReqMessage(subscriberId, new Filters(
            new HashtagTagFilter(new HashtagTag(hashTagString)))),
        relayUrl);
    assertEquals(1, baseMessages.size());
    assertTrue(baseMessages
        .stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
  }

  @Test
  void testReqMessagesMatchesGeneric() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String hashTagString = "h-tag-1";

    List<BaseMessage> returnedBaseMessages = new NostrSingleRequestService()
        .send(
            new ReqMessage(subscriberId, new Filters(
                new HashtagTagFilter(new HashtagTag(hashTagString)))),
            relayUrl);

    List<EventIF> events = BaseTextNoteEventMessageIT.getEventIFs(returnedBaseMessages);

    assertFalse(events.isEmpty());
    //    associated event
    assertTrue(events.stream().anyMatch(s -> s.getPublicKey().toHexString().equals(identity.getPublicKey().toHexString())));
    assertTrue(events.stream().anyMatch(s -> s.getTags().stream().anyMatch(tag -> tag.toString().contains(hashTagString))));
  }
}
