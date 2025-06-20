package com.prosilion.superconductor.service.event;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.service.event.service.EventKindServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService implements EventServiceIF {
  private final EventKindServiceIF eventKindService;

  @Autowired
  public EventService(@NonNull EventKindServiceIF eventKindService) {
    this.eventKindService = eventKindService;
  }

  @Override
  public void processIncomingEvent(@NonNull EventMessage eventMessage) {
    log.debug("processing incoming TEXT_NOTE: [{}]", eventMessage);
    eventKindService.processIncomingEvent(eventMessage.getEvent());
  }
}
