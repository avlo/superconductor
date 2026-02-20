package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.event.kind.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.kind.type.EventKindTypeServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
public class EventService implements EventServiceIF {
  private final static String CLASS_STRING_MAP_S = "Class [%s] not found in kindClassStringMap [%s]";
  private final EventPlugin eventPlugin;
  private final EventKindServiceIF eventKindServiceIF;
  private final EventKindTypeServiceIF eventKindTypeServiceIF;
  private final Map<String, String> kindClassStringMap;

  public EventService(
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin,
      @NonNull EventKindServiceIF eventKindServiceIF,
      @NonNull EventKindTypeServiceIF eventKindTypeServiceIF,
      @NonNull Map<String, String> kindClassStringMap) {
    this.eventPlugin = eventPlugin;
    this.eventKindServiceIF = eventKindServiceIF;
    this.eventKindTypeServiceIF = eventKindTypeServiceIF;
    this.kindClassStringMap = kindClassStringMap;
  }

  @Override
  public void processIncomingEvent(@NonNull EventMessage eventMessage) {
    EventIF event = eventMessage.getEvent();
    Kind kind = event.getKind();
    log.debug("processIncomingEvent(EventMessage) kind:[{}]\n{}", kind, event.createPrettyPrintJson());

    Optional<BaseEvent> typedEvent = createTypedEvent(event);
    if (typedEvent.isPresent()) {
      eventPlugin.processIncomingEvent(typedEvent.get());
      return;
    }

//    TODO: simplify below
    if (matchesKind(event) && hasExternalIdentityTag(event)) {
      eventKindTypeServiceIF.processIncomingEvent(event);
      return;
    }

    eventKindServiceIF.processIncomingEvent(event);
  }

  private boolean matchesKind(EventIF event) {
    boolean b = eventKindTypeServiceIF.getKinds().stream().anyMatch(event.getKind()::equals);
    return b;
  }

  private boolean hasExternalIdentityTag(EventIF event) {
    boolean b = !Filterable.getTypeSpecificTags(ExternalIdentityTag.class, event).isEmpty();
    return b;
  }

  public Optional<BaseEvent> createTypedEvent(EventIF eventIF) {
    String lookupKind = eventIF.getKind().getName().toUpperCase();
    Optional<String> optionalLookupKind = Optional.ofNullable(kindClassStringMap.get(lookupKind));

    if (optionalLookupKind.isEmpty())
      return Optional.empty();

    Class<? extends BaseEvent> aClass = null;
    try {
      aClass = (Class<? extends BaseEvent>) Class.forName(optionalLookupKind.get());
    } catch (ClassNotFoundException e) {
      throw lookupKindNotFound(lookupKind);
    }

    Class<? extends BaseEvent> finalAClass = aClass;
    Optional<BaseEvent> baseEvent = optionalLookupKind.map(s ->
        createTypedSimpleEvent(
            eventIF.asGenericEventRecord(),
            finalAClass));

    return baseEvent;
  }

  private NostrException lookupKindNotFound(String lookupKind) {
    return new NostrException(
        String.format(CLASS_STRING_MAP_S, lookupKind, kindClassStringMap));
  }
}
