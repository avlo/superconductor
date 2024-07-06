package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.pubsub.RemoveSubscriberFilterEvent;
import com.prosilion.superconductor.service.AbstractSubscriberService;
import com.prosilion.superconductor.service.request.NoExistingUserException;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.CloseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CloseMessageService<T extends CloseMessage> implements MessageService<T> {
  @Getter
  public final String command = "CLOSE";
  private final ApplicationEventPublisher publisher;
  private final AbstractSubscriberService abstractSubscriberService;

  @Autowired
  public CloseMessageService(AbstractSubscriberService abstractSubscriberService, ApplicationEventPublisher publisher) {
    this.publisher = publisher;
    this.abstractSubscriberService = abstractSubscriberService;
  }

  @Override
  public void processIncoming(@NonNull T closeMessage, @NonNull String sessionId) {
    log.info("processing CLOSE event");
    removeSubscriberBySubscriberId(closeMessage.getSubscriptionId());
  }

  public void removeSubscriberBySessionId(@NonNull String sessionId) {
    List<Long> subscriberBySessionId = abstractSubscriberService.removeSubscriberBySessionId(sessionId);
    subscriberBySessionId.forEach(subscriber -> publisher.publishEvent(new RemoveSubscriberFilterEvent(
        subscriber)));
  }

  public void removeSubscriberBySubscriberId(@NonNull String subscriberId) {
    try {
      publisher.publishEvent(
          new RemoveSubscriberFilterEvent(
              abstractSubscriberService.removeSubscriberBySubscriberId(subscriberId)));
    } catch (NoExistingUserException e) {
      log.info("no match to remove for subscriberId [{}]", subscriberId);
    }
  }
//  public void deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
//    subscriberService.deactivateSubscriberBySessionId(sessionId);
//  }
}
