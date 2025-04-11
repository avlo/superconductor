package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.PublicKey;
import nostr.event.filter.AuthorFilter;
import nostr.event.filter.EventFilter;
import nostr.event.filter.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;
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

  private final static String authorPubKey = Factory.generateRandomHex64String();
  private final static String eventId = Factory.generateRandomHex64String();
  private final static String globalSubscriberId = Factory.generateRandomHex64String(); // global subscriber UUID
  private final String content;

  @Autowired
  EventMessageIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    this.content = Factory.lorumIpsum(getClass());

    String globalEventJson = "[\"EVENT\",{\"id\":\"" + eventId + "\",\"kind\":1,\"content\":\"" + content + "\",\"pubkey\":\"" + authorPubKey + "\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
    log.debug("setup() send event:\n  {}", globalEventJson);

    this.nostrRelayService.createEvent(globalEventJson);
    assertFalse(this.nostrRelayService.getEvents().isEmpty());
  }

  @Test
  void testReqFilteredByEventAndAuthorViaReqMessage() throws JsonProcessingException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter<GenericEvent> eventFilter = new EventFilter<>(new GenericEvent(eventId));
    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(new PublicKey(authorPubKey));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));
    List<String> returnedEvents = nostrRelayService.sendRequestReturnEvents(reqMessage);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.contains(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.contains(content)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.contains(authorPubKey)));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter, authorFilter));
    List<String> returnedEvents2 = nostrRelayService.sendRequestReturnEvents(reqMessage2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.contains(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.contains(content)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.contains(authorPubKey)));
  }

  @Test
  void testReqFilteredByEventId() throws JsonProcessingException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter<GenericEvent> eventFilter = new EventFilter<>(new GenericEvent(eventId));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));
    List<String> returnedEvents = nostrRelayService.sendRequestReturnEvents(reqMessage);

    log.debug("okMessage to testReqFilteredByEventId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.contains(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.contains(content)));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter));
    List<String> returnedEvents2 = nostrRelayService.sendRequestReturnEvents(reqMessage2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.contains(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.contains(content)));
  }

  @Test
  void testReqFilteredByAuthor() throws JsonProcessingException {
    final String subscriberId = Factory.generateRandomHex64String();

    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(new PublicKey(authorPubKey));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));
    List<String> returnedEvents = nostrRelayService.sendRequestReturnEvents(reqMessage);

    log.debug("okMessage to testReqFilteredByAuthor:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.contains(authorPubKey)));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(authorFilter));
    List<String> returnedEvents2 = nostrRelayService.sendRequestReturnEvents(reqMessage2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.contains(authorPubKey)));
  }

  @Test
  void testReqNonMatchingEvent() throws JsonProcessingException {
    String nonMatchingSubscriberId = Factory.generateRandomHex64String();
    String nonMatchingEventId = Factory.generateRandomHex64String();

    EventFilter<GenericEvent> eventFilter = new EventFilter<>(new GenericEvent(nonMatchingEventId));

    ReqMessage reqMessage = new ReqMessage(nonMatchingSubscriberId, new Filters(eventFilter));

    List<String> returnedEvents = nostrRelayService.sendRequestReturnEvents(reqMessage);
    log.debug("okMessage:");
    log.debug("  " + returnedEvents);
    assertFalse(returnedEvents.stream().anyMatch(event -> event.contains(nonMatchingEventId)));
  }
}
