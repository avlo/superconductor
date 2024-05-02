package com.prosilion.nostrrelay.service.message;

import com.prosilion.nostrrelay.pubsub.RemoveSubscriberFilterEvent;
import com.prosilion.nostrrelay.service.request.SubscriberService;
import lombok.extern.java.Log;
import nostr.event.message.CloseMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

@Log
@Service
public class CloseMessageService<T extends CloseMessage> {
  private final ApplicationEventPublisher publisher;
  private final SubscriberService subscriberService;

  public CloseMessageService(
      SubscriberService subscriberService,
      ApplicationEventPublisher publisher) {
    this.publisher = publisher;
    this.subscriberService = subscriberService;
  }

  public void processIncoming(T closeMessage) {
    log.log(Level.INFO, "processing CLOSE event");
    removeSubscriberBySubscriberId(closeMessage.getSubscriptionId());
  }

  public void removeSubscriberBySessionId(String sessionId) {
    List<Long> subscriberBySessionId = subscriberService.removeSubscriberBySessionId(sessionId);
    subscriberBySessionId.forEach(subscriber -> publisher.publishEvent(new RemoveSubscriberFilterEvent(
        subscriber)));
  }

  public void removeSubscriberBySubscriberId(String subscriberId) {
    RemoveSubscriberFilterEvent removeSubscriberFilterEvent = new RemoveSubscriberFilterEvent(
        subscriberService.removeSubscriberBySubscriberId(subscriberId));
    publisher.publishEvent(removeSubscriberFilterEvent);
//    CloseMessage message = NIP01.createCloseMessage(String.valueOf(removeSubscriberFilterEvent.subscriberId()));
//    eventService.publishEvent(new BroadcastMessageEvent<>(sessionId, message));
  }

//  public void deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
//    subscriberService.deactivateSubscriberBySessionId(sessionId);
//  }
}
