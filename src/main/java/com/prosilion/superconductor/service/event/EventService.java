package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.service.event.type.EventTypeServiceIF;
import com.prosilion.superconductor.service.request.NotifierService;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
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
  private final NotifierService<T> notifierService;

  @Autowired
  public EventService(
      NotifierService<T> notifierService,
      EventTypeServiceIF<T> eventTypeService) {
    this.notifierService = notifierService;
    this.eventTypeService = eventTypeService;
  }

  //  @Async
  @Override
  public <U extends EventMessage> void processIncomingEvent(@NonNull U eventMessage) {
    log.debug("processing incoming TEXT_NOTE: [{}]", eventMessage);
    T event = (T) eventMessage.getEvent();
    eventTypeService.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent<>(event));
  }
}
