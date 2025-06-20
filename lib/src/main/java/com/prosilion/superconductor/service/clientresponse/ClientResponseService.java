package com.prosilion.superconductor.service.clientresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import lombok.extern.slf4j.Slf4j;
import com.prosilion.nostr.event.GenericEventKindIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
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
    processOkClientResponse(sessionId, eventMessage, "success: request processed");
  }

  public void processOkClientResponse(@NonNull String sessionId, @NonNull EventMessage eventMessage, @NonNull String reason) {
    log.debug("Processing event message: {}, reason: {}", eventMessage.getEvent(), reason);
    try {
      publisher.publishEvent(new ClientOkResponse(sessionId, (GenericEventKindIF) eventMessage.getEvent(), true, reason));
    } catch (JsonProcessingException e) {
      processNotOkClientResponse(sessionId, eventMessage, e.getMessage());
    }
  }

  public void processCloseClientResponse(@NonNull String sessionId) {
    log.debug("Processing close: {}", sessionId);
    try {
      publisher.publishEvent(new ClientCloseResponse(sessionId));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"CLOSE\", \"" + sessionId + "\"]"
      ));
    }
  }

  public void processNotOkClientResponse(@NonNull String sessionId, @NonNull EventMessage eventMessage, @NonNull String reason) {
    log.debug("Processing failed event message: {}, reason: {}", eventMessage.getEvent(), reason);
    try {
      publisher.publishEvent(new ClientOkResponse(sessionId, (GenericEventKindIF) eventMessage.getEvent(), false, reason));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"OK\", \"" + eventMessage.getEvent().getId() + "\", false, \"" + e.getMessage() + "\"]"
      ));
    }
  }

  public void processNoticeClientResponse(@NonNull ReqMessage reqMessage, @NonNull String sessionId, @NonNull String reason, boolean valid) {
    log.debug("Processing failed request message: {}, reason: {}", reqMessage, reason);
    try {
      publisher.publishEvent(new ClientNoticeResponse(sessionId, reason, valid));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"NOTICE\", \"" + reqMessage + "\"]"
      ));
    }
  }
}
