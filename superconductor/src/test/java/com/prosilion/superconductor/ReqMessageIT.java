package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.PublicKey;
import nostr.event.BaseMessage;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import static com.prosilion.superconductor.EventMessageIT.getGenericEvents;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = {"/reqmessageit.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS) // class level @Sql
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class ReqMessageIT {
  private final NostrRelayService nostrRelayService;

  @Autowired
  ReqMessageIT(@NonNull NostrRelayService nostrRelayService) {
    this.nostrRelayService = nostrRelayService;
  }

  @Test
  void testReqFilteredByEventAndAuthor() throws JsonProcessingException {
    final String subscriberId = Factory.generateRandomHex64String();

    String eventId = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
    String authorPubkey = "dddeeef81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    EventFilter<GenericEvent> eventFilter = new EventFilter<>(new GenericEvent(eventId));
    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(new PublicKey(authorPubkey));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEvent> returnedGenericEvents = getGenericEvents(returnedBaseMessages);

    assertTrue(returnedGenericEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedGenericEvents.stream().anyMatch(event -> event.getPubKey().toHexString().equals(authorPubkey)));
  }

  @Test
  void testReqFilteredByEventId() throws JsonProcessingException {
    final String subscriberId = Factory.generateRandomHex64String();
    String eventId = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
    EventFilter<GenericEvent> eventFilter = new EventFilter<>(new GenericEvent(eventId));
    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEvent> returnedGenericEvents = getGenericEvents(returnedBaseMessages);

    assertTrue(returnedGenericEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
  }

  @Test
  void testReqFilteredByAuthor() throws JsonProcessingException {
    String authorPubkey = "dddeeef81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
    String subscriberId = Factory.generateRandomHex64String();

    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(new PublicKey(authorPubkey));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEvent> returnedGenericEvents = getGenericEvents(returnedBaseMessages);

    assertTrue(returnedGenericEvents.stream().anyMatch(event -> event.getPubKey().toHexString().equals(authorPubkey)));
  }
}
