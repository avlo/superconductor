package com.prosilion.superconductor.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.base.util.NostrComprehensiveClient;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseReqMessageIT {
  private final NostrComprehensiveClient nostrComprehensiveClient;
  private final String eventId;
  private final PublicKey authorPubkey;

  public BaseReqMessageIT(
      @NonNull String relayUrl,
      Duration requestTimeoutDuration) throws IOException {
    this.nostrComprehensiveClient = new NostrComprehensiveClient(relayUrl, requestTimeoutDuration);
    Identity author = Identity.generateRandomIdentity();
    this.authorPubkey = author.getPublicKey();

    TextNoteEvent event = new TextNoteEvent(author, Factory.lorumIpsum(getClass()));
    this.eventId = event.getId();

    EventMessage eventMessage = new EventMessage(event);
    assertTrue(
        this.nostrComprehensiveClient
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

    List<BaseMessage> returnedBaseMessages = nostrComprehensiveClient.send(reqMessage);
    List<EventIF> returnedEventIFs = BaseTextNoteEventMessageIT.getEventIFs(returnedBaseMessages);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorPubkey)));
  }

  @Test
  void testReqFilteredByEventId() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();
    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));
    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));

    List<BaseMessage> returnedBaseMessages = nostrComprehensiveClient.send(reqMessage);
    List<EventIF> returnedEventIFs = BaseTextNoteEventMessageIT.getEventIFs(returnedBaseMessages);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
  }

  @Test
  void testReqFilteredByAuthor() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    AuthorFilter authorFilter = new AuthorFilter(authorPubkey);

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));

    List<BaseMessage> returnedBaseMessages = nostrComprehensiveClient.send(reqMessage);
    List<EventIF> returnedEventIFs = BaseTextNoteEventMessageIT.getEventIFs(returnedBaseMessages);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorPubkey)));
  }
}
