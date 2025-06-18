package com.prosilion.superconductor.service.event;

import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.service.event.service.EventKindServiceIF;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService<T extends GenericEventDtoIF> implements EventServiceIF<T> {
  private final EventKindServiceIF<T> eventKindService;

  @Autowired
  public EventService(@NonNull EventKindServiceIF<T> eventKindService) {
    this.eventKindService = eventKindService;
  }

  @Override
  public <U extends EventMessage> void processIncomingEvent(@NonNull U eventMessage) {
    log.debug("processing incoming TEXT_NOTE: [{}]", eventMessage);
    eventKindService.processIncomingEvent((T) eventMessage.getEvent());
  }
}
