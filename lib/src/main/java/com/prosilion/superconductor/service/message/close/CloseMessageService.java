package com.prosilion.superconductor.service.message.close;

import com.prosilion.superconductor.service.request.pubsub.RemoveSubscriberFilter;
import com.prosilion.superconductor.service.request.AbstractSubscriberService;
import com.prosilion.superconductor.util.NoExistingUserException;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.CloseMessage;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

@Slf4j
public class CloseMessageService<T extends CloseMessage> implements CloseMessageServiceIF<T> {
  @Getter
  public final String command = "CLOSE";
  private final ApplicationEventPublisher publisher;
  private final AbstractSubscriberService abstractSubscriberService;

  public CloseMessageService(
      AbstractSubscriberService abstractSubscriberService,
      ApplicationEventPublisher publisher) {
    this.publisher = publisher;
    this.abstractSubscriberService = abstractSubscriberService;
  }

  @Override
  public void processIncoming(@NonNull T closeMessage, @NonNull String sessionId) {
    log.debug("processing CLOSE event, sessionId [{}]", sessionId);
    removeSubscriberBySessionId(sessionId);
  }

  @Override
  public void closeSession(@NonNull String sessionId) {
  }

  @Override
  public void removeSubscriberBySessionId(@NonNull String sessionId) {
    List<Long> subscriberBySessionId = abstractSubscriberService.removeSubscriberBySessionId(sessionId);
    // TODO: no publishers bound to below?
    subscriberBySessionId.forEach(subscriber -> publisher.publishEvent(new RemoveSubscriberFilter(subscriber)));
  }

  @Override
  public void removeSubscriberBySubscriberId(@NonNull String subscriberId) {
    try {
      publisher.publishEvent(
          new RemoveSubscriberFilter(
              abstractSubscriberService.removeSubscriberBySubscriberId(subscriberId)));
    } catch (NoExistingUserException e) {
      log.debug("no match to remove for subscriptionHash [{}]", subscriberId);
    }
  }
//  public void deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
//    subscriberService.deactivateSubscriberBySessionId(sessionId);
//  }
}
