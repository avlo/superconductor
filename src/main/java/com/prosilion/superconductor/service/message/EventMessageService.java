package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.event.EventServiceIF;
import com.prosilion.superconductor.service.okresponse.ClientOkResponseService;
import com.prosilion.superconductor.service.okresponse.FailedOkResponseException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
public class EventMessageService<T extends EventMessage> implements MessageService<T> {
  public final String command = "EVENT";
  private final Map<Kind, EventServiceIF<T>> kindEventServiceMap;
  private final ClientOkResponseService<T> okResponseService;

  @Autowired
  public EventMessageService(List<EventServiceIF<T>> eventServices, ClientOkResponseService<T> okResponseService) {
    this.kindEventServiceMap = new EnumMap<>(eventServices.stream().collect(Collectors.toMap(EventServiceIF<T>::getKind, Function.identity())));
    this.okResponseService = okResponseService;
  }

  @Override
  public void processIncoming(@NotNull T eventMessage, String sessionId) {
    log.info("EVENT message NIP: {}", eventMessage.getNip());
    var kind = ((GenericEvent) eventMessage.getEvent()).getKind();
    log.info("EVENT message type: {}", eventMessage.getEvent());
    try {
      processOkClientResponse(sessionId, eventMessage);
      createEventService(Kind.valueOf(kind), eventMessage);
    } catch (FailedOkResponseException e) {
      log.info("FAILED event message: {}", e.getMessage());
    }
  }

  private void createEventService(@NotNull Kind kind, T eventMessage) {
    kindEventServiceMap.get(kind).processIncoming(eventMessage);
  }

  private void processOkClientResponse(String sessionId, T eventMessage) throws FailedOkResponseException {
    okResponseService.processOkClientResponse(sessionId, eventMessage);
  }
}