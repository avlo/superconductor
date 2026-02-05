package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our BasicCar
public class EventKindPlugin<T extends BaseEvent> implements EventKindPluginIF<T> {
  @Getter
  private final Kind kind;
  private final EventPluginIF<T> eventPlugin;

  public EventKindPlugin(@NonNull Kind kind, @NonNull EventPluginIF<T> eventPlugin) {
    this.kind = kind;
    this.eventPlugin = eventPlugin;
  }

  @Override
  public void processIncomingEvent(T event) {
    eventPlugin.processIncomingEvent(event);
  }
}
