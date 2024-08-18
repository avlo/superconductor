package com.prosilion.superconductor.service.okresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

@Slf4j
@Service
public class ClientOkResponseService {
  private final ApplicationEventPublisher publisher;

  @Autowired
  public ClientOkResponseService(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public void processOkClientResponse(@NonNull String sessionId, @NonNull EventMessage eventMessage) {
    log.info("Processing event message: {}", eventMessage.getEvent());
    processOkClientResponse(sessionId, eventMessage, "success: request processed");
  }

  public void processOkClientResponse(@NonNull String sessionId, @NonNull EventMessage eventMessage, @NonNull String reason) {
    log.info("Processing event message: {}, reason: {}", eventMessage.getEvent(), reason);
    try {
      publisher.publishEvent(new OkClientResponse(sessionId, (GenericEvent) eventMessage.getEvent(), true, reason));
    } catch (JsonProcessingException e) {
      processNotOkClientResponse(sessionId, eventMessage, e.getMessage());
    }
  }

  public void processNotOkClientResponse(@NonNull String sessionId, @NonNull EventMessage eventMessage, @NonNull String reason) {
    log.info("Processing failed event message: {}, reason: {}", eventMessage.getEvent(), reason);
    try {
      publisher.publishEvent(new OkClientResponse(sessionId, (GenericEvent) eventMessage.getEvent(), false, reason));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"OK\", \"" + eventMessage.getEvent().getId() + "\", false, \"" + e.getMessage() + "\"]"
      ));
    }
  }
}
