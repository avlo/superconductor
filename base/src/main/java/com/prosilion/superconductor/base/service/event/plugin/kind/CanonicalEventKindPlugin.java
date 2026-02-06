package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class CanonicalEventKindPlugin extends PublishingEventKindPlugin {
  public static final String CLASS_STRING_MAP_S = "Class [%s] not found in kindClassStringMap [%s]";
  private final Map<String, String> kindClassStringMap;

  public CanonicalEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF eventKindPlugin,
      @NonNull Map<String, String> kindClassStringMap) {
    super(notifierService, eventKindPlugin);
    this.kindClassStringMap = kindClassStringMap;
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    super.processIncomingEvent(event);
  }

  @Override
  public BaseEvent materialize(EventIF eventIF) {
    String lookupKind = eventIF.getKind().getName().toUpperCase();

    try {
      return createTypedSimpleEvent(
          eventIF.asGenericEventRecord(),
          (Class<? extends BaseEvent>) Class.forName(
              Optional.ofNullable(
                      kindClassStringMap.get(lookupKind))
                  .orElseThrow(() ->
                      lookupKindNotFound(lookupKind))));
    } catch (ClassNotFoundException e) {
      throw lookupKindNotFound(lookupKind);
    }
  }

  private NostrException lookupKindNotFound(String lookupKind) {
    return new NostrException(
        String.format(CLASS_STRING_MAP_S, lookupKind, kindClassStringMap));
  }
}
