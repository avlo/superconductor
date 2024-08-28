package com.prosilion.superconductor.service.okresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

@Slf4j
@Service
public class ClientResponseService {
  private final ApplicationEventPublisher publisher;

  @Autowired
  public ClientResponseService(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public void processOkClientResponse(@NonNull String sessionId, @NonNull EventMessage eventMessage) {
    log.info("Processing event message: {}", eventMessage.getEvent());
    processOkClientResponse(sessionId, eventMessage, "success: request processed");
  }

  public void processOkClientResponse(@NonNull String sessionId, @NonNull EventMessage eventMessage, @NonNull String reason) {
    log.info("Processing event message: {}, reason: {}", eventMessage.getEvent(), reason);
    try {
      publisher.publishEvent(new ClientOkResponse(sessionId, (GenericEvent) eventMessage.getEvent(), true, reason));
    } catch (JsonProcessingException e) {
      processNotOkClientResponse(sessionId, eventMessage, e.getMessage());
    }
  }

  public void processCloseClientResponse(@NonNull String sessionId) {
    log.info("Processing close: {}", sessionId);
    try {
      publisher.publishEvent(new ClientCloseResponse(sessionId));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"CLOSE\", \"" + sessionId + "\"]"
      ));
    }
  }

  public void processNotOkClientResponse(@NonNull String sessionId, @NonNull EventMessage eventMessage, @NonNull String reason) {
    log.info("Processing failed event message: {}, reason: {}", eventMessage.getEvent(), reason);
    try {
      publisher.publishEvent(new ClientOkResponse(sessionId, (GenericEvent) eventMessage.getEvent(), false, reason));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"OK\", \"" + eventMessage.getEvent().getId() + "\", false, \"" + e.getMessage() + "\"]"
      ));
    }
  }

  public void processNoticeClientResponse(@NonNull ReqMessage reqMessage, @NonNull String sessionId, @NonNull String reason, boolean valid) {
    log.info("Processing failed request message: {}, reason: {}", reqMessage, reason);
    try {
      publisher.publishEvent(new ClientNoticeResponse(sessionId, reason, valid));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"NOTICE\", \"" + reqMessage + "\"]"
      ));
    }
  }
}
