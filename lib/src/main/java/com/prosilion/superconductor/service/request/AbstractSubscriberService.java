package com.prosilion.superconductor.service.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.service.request.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.service.request.pubsub.EoseNotice;
import com.prosilion.superconductor.service.request.pubsub.FireNostrEvent;
import lombok.NonNull;
import nostr.api.NIP01;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EoseMessage;
import org.springframework.context.ApplicationEventPublisher;

public abstract class AbstractSubscriberService implements SubscriberService {
  private final ApplicationEventPublisher publisher;

  protected AbstractSubscriberService(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public <T extends GenericEvent> void broadcastToClients(@NonNull FireNostrEvent<T> fireNostrEvent) throws JsonProcessingException {
    publisher.publishEvent(
        new BroadcastMessageEvent<>(
            get(fireNostrEvent.subscriptionHash()).getSessionId(),
            NIP01.createEventMessage(fireNostrEvent.event(), fireNostrEvent.subscriberId())));
  }

  public void broadcastToClients(@NonNull EoseNotice eoseNotice) throws JsonProcessingException {
    publisher.publishEvent(
        new BroadcastMessageEvent<>(
            get(eoseNotice.subscriptionHash()).getSessionId(),
            new EoseMessage(eoseNotice.subscriberId())));
  }
}
