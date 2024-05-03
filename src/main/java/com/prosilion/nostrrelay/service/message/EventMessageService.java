package com.prosilion.nostrrelay.service.message;

import com.prosilion.nostrrelay.service.event.EventServiceIF;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Getter
@Service
public class EventMessageService<T extends EventMessage> implements MessageService<T> {
  public final String command = "EVENT";
  //  TODO: revisit, EnumMap more approp
  private final Map<Kind, EventServiceIF<T>> kindEventServiceMap;

  @Autowired
  public EventMessageService(List<EventServiceIF<T>> eventServices) {
    this.kindEventServiceMap = eventServices.stream().collect(Collectors.toMap(EventServiceIF<T>::getKind, Function.identity()));
  }

  @Override
  public void processIncoming(@NotNull T eventMessage, String sessionId) {
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    var kind = ((GenericEvent) eventMessage.getEvent()).getKind();
    log.log(Level.INFO, "EVENT message type: {0}", eventMessage.getEvent());
    createEventService(Kind.valueOf(kind), eventMessage);
  }

  private void createEventService(@NotNull Kind kind, T eventMessage) {
    kindEventServiceMap.get(kind).processIncoming(eventMessage);
  }
}