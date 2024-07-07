package com.prosilion.superconductor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import lombok.NonNull;
import nostr.api.NIP01;
import nostr.event.impl.GenericEvent;
import org.springframework.context.ApplicationEventPublisher;

public abstract class AbstractSubscriberService implements SubscriberService {
  private final ApplicationEventPublisher publisher;

  protected AbstractSubscriberService(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public <T extends GenericEvent> void broadcastToClients(@NonNull FireNostrEvent<T> fireNostrEvent) throws JsonProcessingException {
    publisher.publishEvent(
        new BroadcastMessageEvent<>(
            get(fireNostrEvent.subscriberId()).getSessionId(),
            NIP01.createEventMessage(fireNostrEvent.event(), String.valueOf(fireNostrEvent.subscriberId()))));
  }
}
