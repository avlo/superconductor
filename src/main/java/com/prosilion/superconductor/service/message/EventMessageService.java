package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.event.EventServiceIF;
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

  @Autowired
  public EventMessageService(List<EventServiceIF<T>> eventServices) {
    this.kindEventServiceMap = new EnumMap<>(eventServices.stream().collect(Collectors.toMap(EventServiceIF<T>::getKind, Function.identity())));
  }

  @Override
  public void processIncoming(@NotNull T eventMessage, String sessionId) {
    log.info("EVENT message NIP: {}", eventMessage.getNip());
    var kind = ((GenericEvent) eventMessage.getEvent()).getKind();
    log.info("EVENT message type: {}", eventMessage.getEvent());
    createEventService(Kind.valueOf(kind), eventMessage);
  }

  private void createEventService(@NotNull Kind kind, T eventMessage) {
    kindEventServiceMap.get(kind).processIncoming(eventMessage);
  }
}