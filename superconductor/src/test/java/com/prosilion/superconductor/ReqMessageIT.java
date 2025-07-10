package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.dto.GenericEventKindDto;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
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

import static com.prosilion.superconductor.TextNoteEventMessageIT.getGenericEventKindIFs;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class ReqMessageIT {
  private final NostrRelayService nostrRelayService;
  private final String eventId;
  private final PublicKey authorPubkey;

  @Autowired
  ReqMessageIT(@NonNull NostrRelayService nostrRelayService) throws NoSuchAlgorithmException, IOException {
    this.nostrRelayService = nostrRelayService;
    Identity author = Identity.generateRandomIdentity();
    this.authorPubkey = author.getPublicKey();

    GenericEventKindIF genericEventDtoIF =
        new GenericEventKindDto(
            new TextNoteEvent(
                author,
                Factory.lorumIpsum(getClass()))).convertBaseEventToGenericEventKindIF();

    this.eventId = genericEventDtoIF.getId();

    EventMessage eventMessage = new EventMessage(genericEventDtoIF);
    assertTrue(
        this.nostrRelayService
            .send(
                eventMessage)
            .getFlag());
  }

  @Test
  void testReqFilteredByEventAndAuthor() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));
    AuthorFilter authorFilter = new AuthorFilter(authorPubkey);

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(returnedBaseMessages);

    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorPubkey)));
  }

  @Test
  void testReqFilteredByEventId() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();
    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));
    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(returnedBaseMessages);

    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
  }

  @Test
  void testReqFilteredByAuthor() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    AuthorFilter authorFilter = new AuthorFilter(authorPubkey);

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(returnedBaseMessages);

    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorPubkey)));
  }
}
