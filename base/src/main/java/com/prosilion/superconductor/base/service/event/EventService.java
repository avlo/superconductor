package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.event.kind.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

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
  public void processIncomingEvent(@NonNull EventMessage eventMessage, @NonNull Relay relay) {
    EventIF event = eventMessage.getEvent();
    Kind kind = event.getKind();
    log.debug("processIncomingEvent(EventMessage) kind:[{}]\n{}", kind, event.createPrettyPrintJson());

//    TODO: simplify below
    if (matchesKindType(event) && hasExternalIdentityTag(event)) {
      eventKindTypeServiceIF.processIncomingEvent(event, relay);
      return;
    }

    if (matchesKind(event)) {
      eventKindServiceIF.processIncomingEvent(event, relay);
      return;
    }

    eventPlugin.processIncomingEvent(event, relay);
  }

  private boolean matchesKind(EventIF event) {
    return eventKindServiceIF.getKinds().stream().anyMatch(event.getKind()::equals);
  }

  private boolean matchesKindType(EventIF event) {
    return eventKindTypeServiceIF.getKinds().stream().anyMatch(event.getKind()::equals);
  }

  private boolean hasExternalIdentityTag(EventIF event) {
    return !event.asGenericEventRecord().getTypeSpecificTags(ExternalIdentityTag.class).isEmpty();
  }
}
