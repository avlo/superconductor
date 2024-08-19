package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.event.EventServiceIF;
import com.prosilion.superconductor.service.okresponse.ClientResponseService;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventMessageService<T extends EventMessage> implements MessageService<T> {
  @Getter
  public final String command = "EVENTXY";
  private final EventServiceIF<T> eventService;
  private final ClientResponseService okResponseService;

  @Autowired
  public EventMessageService(EventServiceIF<T> eventService, ClientResponseService okResponseService) {
    this.eventService = eventService;
    this.okResponseService = okResponseService;
  }

  public void processIncoming(@NotNull T eventMessage, @NonNull String sessionId) {
    log.info("EVENT message NIP: {}", eventMessage.getNip());
    log.info("EVENT message type: {}", eventMessage.getEvent());
    eventService.processIncomingEvent(eventMessage);
    okResponseService.processOkClientResponse(sessionId, eventMessage);
  }
}