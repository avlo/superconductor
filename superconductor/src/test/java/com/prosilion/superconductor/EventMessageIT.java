package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.enums.NostrException;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.dto.EventDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class EventMessageIT {
  private final NostrRelayService nostrRelayService;

  private final static Identity identity = Identity.generateRandomIdentity();
  private final String eventId;
  private final static String globalSubscriberId = Factory.generateRandomHex64String(); // global subscriber UUID
  private final String content;

  @Autowired
  EventMessageIT(@NonNull NostrRelayService nostrRelayService) throws IOException, NostrException, NoSuchAlgorithmException {
    this.nostrRelayService = nostrRelayService;
    this.content = Factory.lorumIpsum(getClass());

    BaseEvent event = new TextNoteEvent(identity, content);
    this.eventId = event.getId();

    assertTrue(
        this.nostrRelayService
            .send(
                new EventMessage(new EventDto(event).convertBaseEventToDto()))
            .getFlag());
  }

  @Test
  void testPass() {
    assertTrue(true);
  }

  @Test
  void testReqSingleSubscriberFilteredByEventAndAuthorViaReqMessage() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter<GenericEventId> eventFilter = new EventFilter<>(new GenericEventId(eventId));
    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventDtoIF> returnedGenericEventDtoIFs = getGenericEventDtoIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedBaseMessages);
    assertTrue(returnedGenericEventDtoIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedGenericEventDtoIFs.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedGenericEventDtoIFs.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqTwoSubscribersFilteredByEventAndAuthorViaReqMessage() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter<GenericEventId> eventFilter = new EventFilter<>(new GenericEventId(eventId));
    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventDtoIF> returnedEvents = getGenericEventDtoIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter, authorFilter));
    List<BaseMessage> returnedBaseMessages2 = nostrRelayService.send(reqMessage2);
    List<GenericEventDtoIF> returnedEvents2 = getGenericEventDtoIFs(returnedBaseMessages2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqFilteredByEventId() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter<GenericEventId> eventFilter = new EventFilter<>(new GenericEventId(eventId));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventDtoIF> returnedEvents = getGenericEventDtoIFs(returnedBaseMessages);

    log.debug("okMessage to testReqFilteredByEventId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getContent().equals(content)));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter));
    List<BaseMessage> returnedBaseMessages2 = nostrRelayService.send(reqMessage2);
    List<GenericEventDtoIF> returnedEvents2 = getGenericEventDtoIFs(returnedBaseMessages2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getContent().equals(content)));
  }

  @Test
  void testReqFilteredByAuthor() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);

    log.debug("okMessage to testReqFilteredByAuthor:");
    log.debug("  " + returnedBaseMessages);
    List<GenericEventDtoIF> returnedEvents = getGenericEventDtoIFs(returnedBaseMessages);

    assertTrue(returnedEvents.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(authorFilter));
    List<BaseMessage> returnedBaseMessages2 = nostrRelayService.send(reqMessage2);
    List<GenericEventDtoIF> returnedEvents2 = getGenericEventDtoIFs(returnedBaseMessages2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqNonMatchingEvent() throws JsonProcessingException, NostrException {
    String nonMatchingSubscriberId = Factory.generateRandomHex64String();
    String nonMatchingEventId = Factory.generateRandomHex64String();

    EventFilter<GenericEventId> eventFilter = new EventFilter<>(new GenericEventId(eventId));

    ReqMessage reqMessage = new ReqMessage(nonMatchingSubscriberId, new Filters(eventFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventDtoIF> returnedEvents = getGenericEventDtoIFs(returnedBaseMessages);
    log.debug("okMessage:");
    log.debug("  " + returnedEvents);
    assertEquals(1, returnedBaseMessages.size());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
    assertTrue(returnedEvents.isEmpty());
  }

  public static List<GenericEventDtoIF> getGenericEventDtoIFs(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .map(GenericEventDtoIF.class::cast)
        .toList();
  }
}
