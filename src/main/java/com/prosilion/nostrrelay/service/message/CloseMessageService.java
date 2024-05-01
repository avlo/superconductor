package com.prosilion.nostrrelay.service.message;

import com.prosilion.nostrrelay.pubsub.CloseMessageEvent;
import com.prosilion.nostrrelay.pubsub.RemoveSubscriberFilterEvent;
import com.prosilion.nostrrelay.service.event.EventService;
import com.prosilion.nostrrelay.service.request.SubscriberService;
import jakarta.persistence.NoResultException;
import lombok.extern.java.Log;
import nostr.event.message.CloseMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Log
@Service
public class CloseMessageService<T extends CloseMessage> {
  private final ApplicationEventPublisher publisher;
  private final SubscriberService subscriberService;
  private final EventService<CloseMessageEvent> eventService;

  public CloseMessageService(
      SubscriberService subscriberService,
      ApplicationEventPublisher publisher,
      EventService<CloseMessageEvent> eventService) {
    this.publisher = publisher;
    this.subscriberService = subscriberService;
    this.eventService = eventService;
  }

  public void processIncoming(T closeMessage, String sessionId) {
    log.log(Level.INFO, "processing CLOSE event");
    removeSubscriberBySessionId(sessionId);
  }

  public void removeSubscriberBySessionId(String sessionId) {
    RemoveSubscriberFilterEvent removeSubscriberFilterEvent = new RemoveSubscriberFilterEvent(
        subscriberService.removeSubscriberBySessionId(sessionId));
    publisher.publishEvent(removeSubscriberFilterEvent);
//    CloseMessage message = NIP01.createCloseMessage(String.valueOf(removeSubscriberFilterEvent.subscriberId()));
//    eventService.publishEvent(new BroadcastMessageEvent<>(sessionId, message));
  }

  public void deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
    subscriberService.deactivateSubscriberBySessionId(sessionId);
  }
}
