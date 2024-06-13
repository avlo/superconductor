package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.pubsub.RemoveSubscriberFilterEvent;
import com.prosilion.superconductor.service.request.NoExistingUserException;
import com.prosilion.superconductor.service.request.SubscriberService;
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
  private final ApplicationEventPublisher publisher;
  private final SubscriberService subscriberService;
  public final String command = "CLOSE";

  @Autowired
  public CloseMessageService(
      SubscriberService subscriberService,
      ApplicationEventPublisher publisher) {
    this.publisher = publisher;
    this.subscriberService = subscriberService;
  }

  @Override
  public void processIncoming(@NonNull T closeMessage, @NonNull String sessionId) {
    log.info("processing CLOSE event");
    removeSubscriberBySubscriberId(closeMessage.getSubscriptionId());
  }

  public void removeSubscriberBySessionId(@NonNull String sessionId) {
    List<Long> subscriberBySessionId = subscriberService.removeSubscriberBySessionId(sessionId);
    subscriberBySessionId.forEach(subscriber -> publisher.publishEvent(new RemoveSubscriberFilterEvent(
        subscriber)));
  }

  public void removeSubscriberBySubscriberId(@NonNull String subscriberId) {
    try {
      publisher.publishEvent(
          new RemoveSubscriberFilterEvent(
              subscriberService.removeSubscriberBySubscriberId(subscriberId)));
    } catch (NoExistingUserException e) {
      log.info("no match to remove for subscriberId [{}]", subscriberId);
    }
  }

  @Override
  public String getCommand() {
    return command;
  }
//  public void deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
//    subscriberService.deactivateSubscriberBySessionId(sessionId);
//  }
}
