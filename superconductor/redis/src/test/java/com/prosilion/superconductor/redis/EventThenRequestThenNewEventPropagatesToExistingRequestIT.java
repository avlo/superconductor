package com.prosilion.superconductor.redis;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.subdivisions.client.RequestSubscriber;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.superconductor.util.Factory;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class EventThenRequestThenNewEventPropagatesToExistingRequestIT extends BaseRequestThenEventThenUpdatedRequestReceivesNewEventIT {
  private final RequestSubscriber<BaseMessage> subscriber_1;
  private final String eventId_1;

  public EventThenRequestThenNewEventPropagatesToExistingRequestIT(@NonNull @Value("${superconductor.relay.url}") String relayUrl) {
    super(relayUrl);

    this.subscriber_1 = new RequestSubscriber<>();
    submitAfterImageReqWithSubscriber(authorPubkey, relayUrl, subscriber_1);

    validateSpecificAfterimageRequestResults(subscriber_1, 0);

    TextNoteEvent sameAuthorEvent_1 = new TextNoteEvent(author, Factory.lorumIpsum(getClass()));
    this.eventId_1 = sameAuthorEvent_1.getId();

    EventMessage eventMessage_1 = getEventMessage(sameAuthorEvent_1);
    assertTrue(
       new NostrEventPublisher(relayUrl)
          .send(
             eventMessage_1)
          .getFlag());

    List<EventIF> returnedEvents_1 = validateSpecificAfterimageRequestResults(subscriber_1, 1);
    assertTrue(returnedEvents_1.stream().map(EventIF::getPublicKey).allMatch(authorPubkey::equals));
    assertTrue(returnedEvents_1.stream().map(EventIF::getId).allMatch(eventId_1::equals));
  }

  @Test
  void testEventThenRequestThenNewEventPropagatesToExistingRequest() throws NostrException {
    TextNoteEvent sameAuthorEvent_2 = new TextNoteEvent(author, Factory.lorumIpsum(getClass()));
    String eventId_2 = sameAuthorEvent_2.getId();

    EventMessage eventMessage_2 = getEventMessage(sameAuthorEvent_2);
    assertTrue(
       new NostrEventPublisher(relayUrl)
          .send(
             eventMessage_2)
          .getFlag());

    RequestSubscriber<BaseMessage> subscriber_2 = new RequestSubscriber<>();
    submitAfterImageReqWithSubscriber(authorPubkey, relayUrl, subscriber_2);

    List<EventIF> returnedEvents_2 = validateSpecificAfterimageRequestResults(subscriber_2, 2);
    assertTrue(returnedEvents_2.stream().map(EventIF::getPublicKey).allMatch(authorPubkey::equals));
    assertTrue(returnedEvents_2.stream().map(EventIF::getId).toList().contains(eventId_1));
    assertTrue(returnedEvents_2.stream().map(EventIF::getId).toList().contains(eventId_2));

    List<EventIF> returnedEvents_1_b = validateSpecificAfterimageRequestResults(subscriber_1, 1);
    assertTrue(returnedEvents_1_b.stream().map(EventIF::getPublicKey).allMatch(authorPubkey::equals));
    assertTrue(returnedEvents_1_b.stream().map(EventIF::getId).allMatch(eventId_2::equals));
  }
}
