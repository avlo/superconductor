package com.prosilion.superconductor.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.service.event.AuthenticatedEventService;
import com.prosilion.superconductor.service.okresponse.ClientOkResponseService;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
      if (!authenticatedEventService.processIncomingEvent(eventMessage, sessionId)) {
        log.info("AUTHENTICATED EVENT message failed session authentication");
        okResponseService.processNotOkClientResponse(
            sessionId,
            new EventMessage(eventMessage.getEvent()),
            String.format("session [%s] has not been authenticated", sessionId));
        return;
      }

      okResponseService.processOkClientResponse(sessionId, eventMessage);
    } catch (JsonProcessingException e) {
      log.info("FAILED authenticating event message: {}", e.getMessage());
    }
  }
}