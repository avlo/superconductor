package com.prosilion.nostrrelay.service.message;

import com.prosilion.nostrrelay.pubsub.RemoveSubscriberFilterEvent;
import com.prosilion.nostrrelay.service.request.SubscriberService;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.message.CloseMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;

@Log
@Getter
@Service
public class CloseMessageService<T extends CloseMessage> implements MessageService<T> {
  private final ApplicationEventPublisher publisher;
  private final SubscriberService subscriberService;
  public final String command = "CLOSE";

  public CloseMessageService(
      SubscriberService subscriberService,
      ApplicationEventPublisher publisher) {
    this.publisher = publisher;
    this.subscriberService = subscriberService;
  }

  @Override
  public void processIncoming(T closeMessage, String sessionId) {
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
  }

//  public void deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
//    subscriberService.deactivateSubscriberBySessionId(sessionId);
//  }
}
