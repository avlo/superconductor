package com.prosilion.superconductor.redis.noop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.redis.config.DataLoader;
import com.prosilion.superconductor.redis.util.Factory;
import com.prosilion.superconductor.redis.util.NostrRelayServiceRedis;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("testnoop")
class EventNoOpReqMessageIT {
  private final NostrRelayServiceRedis nostrRelayService;
  private final String eventId;
  private final String authorPubkey;

  @Autowired
  EventNoOpReqMessageIT(
      @NonNull NostrRelayServiceRedis nostrRelayService,
      @NonNull DataLoader testDataLoader) {
    this.nostrRelayService = nostrRelayService;
    this.eventId = testDataLoader.getUpvoteBadgeDefinitionEvent().getId();
    this.authorPubkey = testDataLoader.getUpvoteBadgeDefinitionEvent().getPublicKey().toString();
  }

  @Test
  void testReqFilteredByEventAndAuthor() throws JsonProcessingException, NostrException {
//    test 1
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));
    AuthorFilter authorFilter = new AuthorFilter(new PublicKey(authorPubkey));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(returnedBaseMessages);

    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getPublicKey().toHexString().equals(authorPubkey)));

//    test 2
    final String subscriberId2 = Factory.generateRandomHex64String();
    EventFilter eventFilter2 = new EventFilter(new GenericEventId(eventId));
    ReqMessage reqMessage2 = new ReqMessage(subscriberId2, new Filters(eventFilter2));

    List<BaseMessage> returnedBaseMessages2 = nostrRelayService.send(reqMessage2);
    List<GenericEventKindIF> returnedGenericEventKindIFs2 = getGenericEventKindIFs(returnedBaseMessages2);

    assertTrue(returnedGenericEventKindIFs2.stream().anyMatch(event -> event.getId().equals(eventId)));

//    test 3
    String subscriberId3 = Factory.generateRandomHex64String();

    AuthorFilter authorFilter3 = new AuthorFilter(new PublicKey(authorPubkey));

    ReqMessage reqMessage3 = new ReqMessage(subscriberId3, new Filters(authorFilter3));

    List<BaseMessage> returnedBaseMessages3 = nostrRelayService.send(reqMessage3);
    List<GenericEventKindIF> returnedGenericEventKindIFs3 = getGenericEventKindIFs(returnedBaseMessages3);

    assertTrue(returnedGenericEventKindIFs3.stream().anyMatch(event -> event.getPublicKey().toHexString().equals(authorPubkey)));
  }

  private List<GenericEventKindIF> getGenericEventKindIFs(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .toList();
  }
}
