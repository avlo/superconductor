package com.prosilion.superconductor.base.service.clientresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.util.Util;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
    log.debug("Processing Ok ClientResponse EventMessage:\n{}", eventMessage.getEvent().createPrettyPrintJson());
    try {
      publisher.publishEvent(new ClientOkResponse(sessionId, eventMessage.getEvent(), true, reason));
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
          "[\"CLOSED\", \"" + sessionId + "\"]"
      ));
    }
  }

  public void processClientClosedResponse(@NonNull String sessionId, @NonNull String reason) {
    log.debug("Processing close: sessionId [{}], reason [{}]", sessionId, reason);
    try {
      publisher.publishEvent(new ClientClosedResponse(sessionId, reason));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"CLOSED\", \"" + sessionId + "\", \"" + reason + "\"]"
      ));
    }
  }

  public void processNotOkClientResponse(@NonNull String sessionId, @NonNull EventMessage eventMessage, @NonNull String reason) {
    log.debug("Processing Not OK ClientResponse EventMessage:\n{}\nreason:\n  [{}]",
        eventMessage.getEvent().createPrettyPrintJson(),
        reason);
    try {
      publisher.publishEvent(new ClientOkResponse(sessionId, eventMessage.getEvent(), false, reason));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"OK\", \"" + eventMessage.getEvent().getId() + "\", false, \"" + e.getMessage() + "\"]"
      ));
    }
  }

  public void processClientNoticeResponse(@NonNull ReqMessage reqMessage, @NonNull String sessionId, @NonNull String reason, boolean valid) {
    log.debug("Processing Notice (failed) request message:\n  {}\nreason:\n  {}",
        getFiltersString(reqMessage.getFiltersList()),
        reason);
    try {
      publisher.publishEvent(new ClientNoticeResponse(sessionId, reason, valid));
    } catch (JsonProcessingException e) {
      publisher.publishEvent(new TextMessage(
          "[\"NOTICE\", \"" + reqMessage + "\"]"
      ));
    }
  }

  private String getFiltersString(List<Filters> filtersList) {
    return filtersList.stream()
        .map(Filters::toString)
        .collect(Collectors.joining(",\n  "));
  }
}
