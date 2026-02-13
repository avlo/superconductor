package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.event.kind.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeServiceIF;
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
    EventIF event = eventMessage.getEvent();
    log.debug("{} processIncomingEvent(EventMessage):\n{}\nkind: [{}]",
        getClass().getSimpleName(),
        event.createPrettyPrintJson(),
        event.getKind());

//    TODO: simplify below
    if (matchesKind(event) && hasExternalIdentityTag(event)) {
      eventKindTypeService.processIncomingEvent(event);
      return;
    }

    eventKindServiceIF.processIncomingEvent(event);
  }

  private boolean matchesKind(EventIF event) {
    boolean b = eventKindTypeService.getKinds().stream().anyMatch(event.getKind()::equals);
    return b;
  }

  private boolean hasExternalIdentityTag(EventIF event) {
    boolean b = !Filterable.getTypeSpecificTags(ExternalIdentityTag.class, event).isEmpty();
    return b;
  }
}
