package com.prosilion.superconductor.service.message.close;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.message.CloseMessage;
import com.prosilion.superconductor.service.request.AbstractSubscriberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
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
    closeSession(sessionId);
    removeSubscriberBySessionId(sessionId);
  }

  @Override
  public void closeSession(@NonNull String sessionId) {
    closeMessageService.closeSession(sessionId);
  }

  @Override
  public void removeSubscriberBySessionId(@NonNull String sessionId) {
    closeMessageService.removeSubscriberBySessionId(sessionId);
  }

  public void removeSubscriberBySubscriberId(@NonNull String subscriberId) {
    closeMessageService.removeSubscriberBySubscriberId(subscriberId);
  }

  @Override
  public Command getCommand() {
    return closeMessageService.getCommand();
  }
}
