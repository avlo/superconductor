package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.AbstractSubscriberService;
import com.prosilion.superconductor.service.event.AuthEntityService;
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
    havingValue = "true")
public class CloseMessageServiceAuthDecorator<T extends CloseMessage> implements CloseMessageServiceIF<T> {
  private final CloseMessageServiceIF<T> closeMessageService;
  private final AuthEntityService authEntityService;

  @Autowired
  public CloseMessageServiceAuthDecorator(
      AuthEntityService authEntityService,
      AbstractSubscriberService abstractSubscriberService,
      ApplicationEventPublisher publisher) {
    this.closeMessageService = new CloseMessageService<>(abstractSubscriberService, publisher);
    this.authEntityService = authEntityService;
  }

  @Override
  public void processIncoming(@NonNull T closeMessage, @NonNull String sessionId) {
    log.info("processing AUTH CLOSE event, sessionId [{}]", sessionId);
    closeMessageService.processIncoming(closeMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return closeMessageService.getCommand();
  }

  @Override
  public void closeSession(@NonNull String sessionId) {
    authEntityService.removeAuthEntityBySessionId(sessionId);
  }

  @Override
  public void removeSubscriberBySessionId(@NonNull String sessionId) {
    closeMessageService.removeSubscriberBySessionId(sessionId);
    closeSession(sessionId);
  }

  @Override
  public void removeSubscriberBySubscriberId(@NonNull String subscriberId) {
  }
}
