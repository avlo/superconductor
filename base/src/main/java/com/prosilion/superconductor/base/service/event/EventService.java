package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.base.service.event.service.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.service.EventKindTypeServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventService implements EventServiceIF {
  private final EventKindServiceIF eventKindServiceIF;
  private final EventKindTypeServiceIF eventKindTypeService;

  public EventService(@NonNull EventKindServiceIF eventKindServiceIF, @NonNull EventKindTypeServiceIF eventKindTypeService) {
    this.eventKindServiceIF = eventKindServiceIF;
    this.eventKindTypeService = eventKindTypeService;
  }

  @Override
  public void processIncomingEvent(@NonNull EventMessage eventMessage) {
    log.debug("processing incoming TEXT_NOTE: [{}]", eventMessage);

    if (eventKindTypeService.getKinds().stream().anyMatch(eventMessage.getEvent().getKind()::equals)) {
      eventKindTypeService.processIncomingEvent(eventMessage.getEvent());
      return;
    }

    eventKindServiceIF.processIncomingEvent(eventMessage.getEvent());
  }
}
