package com.prosilion.superconductor.redis;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.subdivisions.client.RequestSubscriber;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.util.Factory;
import java.util.List;
import lombok.NonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseRequestThenEventThenUpdatedRequestReceivesNewEventIT {
  protected final Identity author = Identity.generateRandomIdentity();
  protected final PublicKey authorPubkey = author.getPublicKey();
  protected final String relayUrl;

  public BaseRequestThenEventThenUpdatedRequestReceivesNewEventIT(String relayUrl) {
    this.relayUrl = relayUrl;
  }

  @NonNull
  protected EventMessage getEventMessage(TextNoteEvent firstEventSameAuthor) {
    return new EventMessage(firstEventSameAuthor);
  }

  protected void submitAfterImageReqWithSubscriber(PublicKey authorPubkey, String url, RequestSubscriber<BaseMessage> subscriber) {
    new NostrSingleRequestService().send(createRequestMessage(authorPubkey), url, subscriber);
  }

  private ReqMessage createRequestMessage(PublicKey authorPubkey) throws NostrException {
    final String subscriberId = Factory.generateRandomHex64String();
    return new ReqMessage(
       subscriberId,
       new Filters(
          new KindFilter(
             Kind.TEXT_NOTE),
          new AuthorFilter(authorPubkey)));
  }

  protected List<EventIF> validateSpecificAfterimageRequestResults(RequestSubscriber<BaseMessage> subscriber, int count) {
    List<EventIF> events =
       getGenericEvents(subscriber.getItems());
    assertEquals(count, (long) events.size());
    return events;
  }

  protected List<EventIF> getGenericEvents(List<BaseMessage> messages) {
    return messages.stream()
       .filter(EventMessage.class::isInstance)
       .map(EventMessage.class::cast)
       .map(EventMessage::getEvent)
       .toList();
  }
}
