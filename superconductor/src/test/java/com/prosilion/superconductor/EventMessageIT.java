package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.api.NIP01;
import nostr.base.PublicKey;
import nostr.event.filter.AuthorFilter;
import nostr.event.filter.EventFilter;
import nostr.event.filter.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import nostr.event.message.ReqMessage;
import nostr.id.Identity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class EventMessageIT {
  private final NostrRelayService nostrRelayService;

  private final static Identity identity = Identity.generateRandomIdentity();
  private final static String eventId = Factory.generateRandomHex64String();
  private final static String globalSubscriberId = Factory.generateRandomHex64String(); // global subscriber UUID
  private final String content;

  @Autowired
  EventMessageIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    this.content = Factory.lorumIpsum(getClass());

    assertTrue(
        this.nostrRelayService
            .send(
                new EventMessage(
                    new NIP01<>(identity).createTextNoteEvent(content).sign().getEvent()))
            .getFlag());
  }

  @Test
  void testReqFilteredByEventAndAuthorViaReqMessage() throws JsonProcessingException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter<GenericEvent> eventFilter = new EventFilter<>(new GenericEvent(eventId));
    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));
    List<GenericEvent> returnedEvents = nostrRelayService.send(reqMessage);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getPubKey().equals(identity.getPublicKey())));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter, authorFilter));
    List<GenericEvent> returnedEvents2 = nostrRelayService.send(reqMessage2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getPubKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqFilteredByEventId() throws JsonProcessingException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter<GenericEvent> eventFilter = new EventFilter<>(new GenericEvent(eventId));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));
    List<GenericEvent> returnedEvents = nostrRelayService.send(reqMessage);

    log.debug("okMessage to testReqFilteredByEventId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getContent().equals(content)));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter));
    List<GenericEvent> returnedEvents2 = nostrRelayService.send(reqMessage2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getContent().equals(content)));
  }

  @Test
  void testReqFilteredByAuthor() throws JsonProcessingException {
    final String subscriberId = Factory.generateRandomHex64String();

    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));
    List<GenericEvent> returnedEvents = nostrRelayService.send(reqMessage);

    log.debug("okMessage to testReqFilteredByAuthor:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getPubKey().equals(identity.getPublicKey())));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(authorFilter));
    List<GenericEvent> returnedEvents2 = nostrRelayService.send(reqMessage2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getPubKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqNonMatchingEvent() throws JsonProcessingException {
    String nonMatchingSubscriberId = Factory.generateRandomHex64String();
    String nonMatchingEventId = Factory.generateRandomHex64String();

    EventFilter<GenericEvent> eventFilter = new EventFilter<>(new GenericEvent(nonMatchingEventId));

    ReqMessage reqMessage = new ReqMessage(nonMatchingSubscriberId, new Filters(eventFilter));

    List<GenericEvent> returnedEvents = nostrRelayService.send(reqMessage);
    log.debug("okMessage:");
    log.debug("  " + returnedEvents);
    assertFalse(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
  }
}
