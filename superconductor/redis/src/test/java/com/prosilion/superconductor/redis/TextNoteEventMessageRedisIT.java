package com.prosilion.superconductor.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.lib.redis.dto.GenericDocumentKindDto;
import com.prosilion.superconductor.redis.util.Factory;
import com.prosilion.superconductor.redis.util.NostrRelayServiceRedis;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
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
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class TextNoteEventMessageRedisIT {
  private final NostrRelayServiceRedis nostrRelayServiceRedis;

  private final static Identity identity = Identity.generateRandomIdentity();
  private final String eventId;
  private final static String globalSubscriberId = Factory.generateRandomHex64String(); // global subscriber UUID
  private final String content;

  @Autowired
  TextNoteEventMessageRedisIT(@NonNull NostrRelayServiceRedis nostrRelayServiceRedis) throws IOException, NostrException, NoSuchAlgorithmException {
    this.nostrRelayServiceRedis = nostrRelayServiceRedis;
    this.content = Factory.lorumIpsum(getClass());

//    List<BaseTag> tags = new ArrayList<>();
//    EventTag E_TAG = Factory.createEventTag(TextNoteEventMessageRedisIT.class);
//    tags.add(E_TAG);
//    BaseEvent event = new TextNoteEvent(identity, tags, content);

    BaseEvent event = new TextNoteEvent(identity, content);
    this.eventId = event.getId();

    GenericEventKindIF genericEventDtoIF = new GenericDocumentKindDto(event).convertBaseEventToGenericEventKindIF();
    EventMessage eventMessage = new EventMessage(genericEventDtoIF);
    assertTrue(
        this.nostrRelayServiceRedis
            .send(
                eventMessage)
            .getFlag());
  }

  @Test
  void testReqSingleSubscriberFilteredByEventAndAuthorViaReqMessage() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));
    AuthorFilter authorFilter = new AuthorFilter(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayServiceRedis.send(reqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedBaseMessages);
    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqTwoSubscribersFilteredByEventAndAuthorViaReqMessage() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));
    AuthorFilter authorFilter = new AuthorFilter(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayServiceRedis.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter, authorFilter));
    List<BaseMessage> returnedBaseMessages2 = nostrRelayServiceRedis.send(reqMessage2);
    List<GenericEventKindIF> returnedEvents2 = getGenericEventKindIFs(returnedBaseMessages2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqFilteredByEventId() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayServiceRedis.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    log.debug("okMessage to testReqFilteredByEventId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getContent().equals(content)));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter));
    List<BaseMessage> returnedBaseMessages2 = nostrRelayServiceRedis.send(reqMessage2);
    List<GenericEventKindIF> returnedEvents2 = getGenericEventKindIFs(returnedBaseMessages2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getContent().equals(content)));
  }

  @Test
  void testReqFilteredByAuthor() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    AuthorFilter authorFilter = new AuthorFilter(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayServiceRedis.send(reqMessage);

    log.debug("okMessage to testReqFilteredByAuthor:");
    log.debug("  " + returnedBaseMessages);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    assertTrue(returnedEvents.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(authorFilter));
    List<BaseMessage> returnedBaseMessages2 = nostrRelayServiceRedis.send(reqMessage2);
    List<GenericEventKindIF> returnedEvents2 = getGenericEventKindIFs(returnedBaseMessages2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqNonMatchingEvent() throws JsonProcessingException, NostrException {
    String nonMatchingSubscriberId = Factory.generateRandomHex64String();
    String nonMatchingEventId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(nonMatchingEventId));

    ReqMessage reqMessage = new ReqMessage(nonMatchingSubscriberId, new Filters(eventFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayServiceRedis.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);
    log.debug("okMessage:");
    log.debug("  " + returnedEvents);
    assertEquals(1, returnedBaseMessages.size());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
    assertTrue(returnedEvents.isEmpty());
  }

  public static List<GenericEventKindIF> getGenericEventKindIFs(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .toList();
  }
}
