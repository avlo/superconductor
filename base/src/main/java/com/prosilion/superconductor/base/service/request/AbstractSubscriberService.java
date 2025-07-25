package com.prosilion.superconductor.base.service.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.base.service.request.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.base.service.request.pubsub.EoseNotice;
import com.prosilion.superconductor.base.service.request.pubsub.FireNostrEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

public abstract class AbstractSubscriberService implements SubscriberService {
  private final ApplicationEventPublisher publisher;

  protected AbstractSubscriberService(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public void broadcastToClients(@NonNull FireNostrEvent fireNostrEvent) throws JsonProcessingException, NostrException {
    publisher.publishEvent(
        new BroadcastMessageEvent<>(
            get(fireNostrEvent.subscriptionHash()).getSessionId(),
            new EventMessage(fireNostrEvent.event(), fireNostrEvent.subscriberId())));
  }

  public void broadcastToClients(@NonNull EoseNotice eoseNotice) throws JsonProcessingException, NostrException {
    publisher.publishEvent(
        new BroadcastMessageEvent<>(
            get(eoseNotice.subscriptionHash()).getSessionId(),
            new EoseMessage(eoseNotice.subscriberId())));
  }
}
