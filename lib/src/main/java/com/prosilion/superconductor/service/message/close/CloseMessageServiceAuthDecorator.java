package com.prosilion.superconductor.service.message.close;

import com.prosilion.superconductor.service.request.AbstractSubscriberService;
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
    log.debug("processing AUTH CLOSE event, sessionId [{}]", sessionId);
    closeSession(sessionId);
    removeSubscriberBySessionId(sessionId);
  }

  @Override
  public void closeSession(@NonNull String sessionId) {
    closeMessageService.closeSession(sessionId);
    authEntityService.removeAuthEntityBySessionId(sessionId);
  }

  @Override
  public void removeSubscriberBySessionId(@NonNull String sessionId) {
    closeMessageService.removeSubscriberBySessionId(sessionId);
  }

  @Override
  public void removeSubscriberBySubscriberId(@NonNull String subscriberId) {
  }

  @Override
  public String getCommand() {
    return closeMessageService.getCommand();
  }
}
