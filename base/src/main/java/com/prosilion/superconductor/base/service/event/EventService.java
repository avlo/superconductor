package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.event.kind.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventService implements EventServiceIF {
  private final EventPlugin eventPlugin;
  private final EventKindServiceIF eventKindServiceIF;
  private final EventKindTypeServiceIF eventKindTypeServiceIF;

  public EventService(
      @NonNull EventPlugin eventPlugin,
      @NonNull EventKindServiceIF eventKindServiceIF,
      @NonNull EventKindTypeServiceIF eventKindTypeServiceIF) {
    this.eventPlugin = eventPlugin;
    this.eventKindServiceIF = eventKindServiceIF;
    this.eventKindTypeServiceIF = eventKindTypeServiceIF;
  }

  @Override
  public void processIncomingEvent(@NonNull EventMessage eventMessage) {
    EventIF event = eventMessage.getEvent();
    Kind kind = event.getKind();
    log.debug("processIncomingEvent(EventMessage) kind:[{}]\n{}", kind, event.createPrettyPrintJson());

//    TODO: simplify below
    if (matchesKindType(event) && hasExternalIdentityTag(event)) {
      eventKindTypeServiceIF.processIncomingEvent(event);
      return;
    }

    if (matchesKind(event)) {
      eventKindServiceIF.processIncomingEvent(event);
      return;
    }

    eventPlugin.processIncomingEvent(event);
  }

  private boolean matchesKind(EventIF event) {
    boolean b = eventKindServiceIF.getKinds().stream().anyMatch(event.getKind()::equals);
    return b;
  }

  private boolean matchesKindType(EventIF event) {
    boolean b = eventKindTypeServiceIF.getKinds().stream().anyMatch(event.getKind()::equals);
    return b;
  }

  private boolean hasExternalIdentityTag(EventIF event) {
    boolean b = !Filterable.getTypeSpecificTags(ExternalIdentityTag.class, event).isEmpty();
    return b;
  }
}
