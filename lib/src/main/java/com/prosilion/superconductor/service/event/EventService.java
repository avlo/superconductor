package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.service.event.type.EventTypeServiceIF;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService<T extends GenericEvent> implements EventServiceIF<T> {
  private final EventTypeServiceIF<T> eventTypeService;

  @Autowired
  public EventService(@NonNull EventTypeServiceIF<T> eventTypeService) {
    this.eventTypeService = eventTypeService;
  }

  @Override
  public <U extends EventMessage> void processIncomingEvent(@NonNull U eventMessage) {
    log.debug("processing incoming TEXT_NOTE: [{}]", eventMessage);
    eventTypeService.processIncomingEvent((T) eventMessage.getEvent());
  }
}
