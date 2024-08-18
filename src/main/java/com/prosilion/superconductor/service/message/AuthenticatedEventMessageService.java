package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.event.AuthenticatedEventService;
import com.prosilion.superconductor.service.okresponse.ClientOkResponseService;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class AuthenticatedEventMessageService<T extends EventMessage> implements MessageService<T> {
  @Getter
  public final String command = "EVENT";
  private final AuthenticatedEventService<T> authenticatedEventService;
  private final ClientOkResponseService okResponseService;

  @Autowired
  public AuthenticatedEventMessageService(AuthenticatedEventService<T> authenticatedEventService, ClientOkResponseService okResponseService) {
    this.authenticatedEventService = authenticatedEventService;
    this.okResponseService = okResponseService;
  }

  public void processIncoming(@NotNull T eventMessage, @NonNull String sessionId) {
    log.info("AUTHENTICATED EVENT message NIP: {}", eventMessage.getNip());
    log.info("AUTHENTICATED EVENT message type: {}", eventMessage.getEvent());
    try {
      authenticatedEventService.processIncomingEvent(eventMessage, sessionId);
    } catch (NoSuchElementException e) {
      log.info("AUTHENTICATED EVENT message failed session authentication");
      okResponseService.processNotOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()),
          String.format("restricted: session [%s] has not been authenticated", sessionId));
      return;
    }
    okResponseService.processOkClientResponse(sessionId, eventMessage);
  }
}