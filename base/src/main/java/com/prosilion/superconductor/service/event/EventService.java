package com.prosilion.superconductor.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.service.event.service.EventKindServiceIF;
import com.prosilion.superconductor.service.event.service.EventKindTypeServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService implements EventServiceIF {
  private final EventKindServiceIF eventKindService;
  private final EventKindTypeServiceIF eventKindTypeService;

  @Autowired
  public EventService(@NonNull EventKindServiceIF eventKindService, @NonNull EventKindTypeServiceIF eventKindTypeService) {
    this.eventKindService = eventKindService;
    this.eventKindTypeService = eventKindTypeService;
  }

  @Override
  public void processIncomingEvent(@NonNull EventMessage eventMessage) {
    log.debug("processing incoming TEXT_NOTE: [{}]", eventMessage);

    if (eventKindTypeService.getKinds().stream().anyMatch(eventMessage.getEvent().getKind()::equals)) {
      eventKindTypeService.processIncomingEvent(eventMessage.getEvent());
      return;
    }

    eventKindService.processIncomingEvent(eventMessage.getEvent());
  }
}
