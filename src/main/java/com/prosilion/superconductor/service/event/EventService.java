package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.service.event.type.EventTypeService;
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
public class EventService<T extends EventMessage> implements EventServiceIF<T> {
  private final EventTypeService<GenericEvent> eventTypeService;
  private final NotifierService<GenericEvent> notifierService;

  @Autowired
  public EventService(
      NotifierService<GenericEvent> notifierService,
      EventTypeService<GenericEvent> eventTypeService) {
    this.notifierService = notifierService;
    this.eventTypeService = eventTypeService;
  }

  //  @Async
  public void processIncomingEvent(@NonNull T eventMessage) {
    log.debug("processing incoming TEXT_NOTE: [{}]", eventMessage);
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    eventTypeService.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent<>(event));
  }
}
