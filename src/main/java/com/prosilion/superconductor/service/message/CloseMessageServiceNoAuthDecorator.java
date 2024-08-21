package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.AbstractSubscriberService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.CloseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "false")
public class CloseMessageServiceNoAuthDecorator<T extends CloseMessage> implements CloseMessageServiceIF<T> {
  private final CloseMessageServiceIF<T> closeMessageService;

  @Autowired
  public CloseMessageServiceNoAuthDecorator(
      AbstractSubscriberService abstractSubscriberService,
      ApplicationEventPublisher publisher) {
    this.closeMessageService = new CloseMessageService<>(abstractSubscriberService, publisher);
  }

  @Override
  public void processIncoming(@NonNull T closeMessage, @NonNull String sessionId) {
    closeMessageService.processIncoming(closeMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return closeMessageService.getCommand();
  }

  @Override
  public void removeSubscriberBySessionId(@NonNull String sessionId) {
    closeMessageService.removeSubscriberBySessionId(sessionId);
  }

  @Override
  public void closeSession(@NonNull String sessionId) {
    closeMessageService.closeSession(sessionId);
  }

  public void removeSubscriberBySubscriberId(@NonNull String subscriberId) {
    closeMessageService.removeSubscriberBySubscriberId(subscriberId);
  }
//  public void deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
//    subscriberService.deactivateSubscriberBySessionId(sessionId);
//  }
}
