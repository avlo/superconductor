package com.prosilion.superconductor.sqlite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.HashtagTagFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.HashtagTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.sqlite.util.Factory;
import com.prosilion.superconductor.sqlite.util.NostrRelayService;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;

import static com.prosilion.superconductor.sqlite.TextNoteEventMessageIT.getGenericEventKindIFs;
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
  MatchingGenericTagSingleLetterQueryIT(@NonNull NostrRelayService nostrRelayService) throws IOException, NostrException, NoSuchAlgorithmException {
    this.nostrRelayService = nostrRelayService;
    BaseEvent textNoteEvent = new TextNoteEvent(identity, List.of(new HashtagTag("h-tag-1")), content);
    assertTrue(
        nostrRelayService
            .send(
                new EventMessage(new GenericEventKindDto(textNoteEvent).convertBaseEventToGenericEventKindIF()))
            .getFlag());
  }

  @Test
  void testReqMessagesNoGenericMatch() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String hashTagString = "textnote-geo-tag-2";

    List<BaseMessage> baseMessages = nostrRelayService.send(
        new ReqMessage(subscriberId, new Filters(
            new HashtagTagFilter(new HashtagTag(hashTagString)))));
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

    List<BaseMessage> returnedBaseMessages = nostrRelayService
        .send(
            new ReqMessage(subscriberId, new Filters(
                new HashtagTagFilter(new HashtagTag(hashTagString)))));

    List<GenericEventKindIF> events = getGenericEventKindIFs(returnedBaseMessages);

    assertFalse(events.isEmpty());
    //    associated event
    assertTrue(events.stream().anyMatch(s -> s.getPublicKey().toHexString().equals(identity.getPublicKey().toHexString())));
    assertTrue(events.stream().anyMatch(s -> s.getTags().stream().anyMatch(tag -> tag.toString().contains(hashTagString))));
  }
}
