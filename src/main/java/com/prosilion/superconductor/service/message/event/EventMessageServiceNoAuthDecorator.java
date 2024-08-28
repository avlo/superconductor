package com.prosilion.superconductor.service.message.event;

import com.prosilion.superconductor.service.event.EventServiceIF;
import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.message.MessageService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "false")
public class EventMessageServiceNoAuthDecorator<T extends EventMessage> implements MessageService<T> {
  private final EventMessageService<T> eventMessageService;

  @Autowired
  public EventMessageServiceNoAuthDecorator(EventServiceIF<T> eventService, ClientResponseService okResponseService) {
    this.eventMessageService = new EventMessageService<>(eventService, okResponseService);
  }

  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    log.info("EVENT message NIP: {}", eventMessage.getNip());
    log.info("EVENT message type: {}", eventMessage.getEvent());
    eventMessageService.processIncoming(eventMessage, sessionId);
    eventMessageService.processOkClientResponse(eventMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return eventMessageService.getCommand();
  }
}