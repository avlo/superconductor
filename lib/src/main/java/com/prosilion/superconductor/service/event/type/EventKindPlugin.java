package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our BasicCar
public class EventKindPlugin implements EventKindPluginIF<Kind> {
  @Getter
  private final Kind kind;
  private final EventPluginIF eventPlugin;

  public EventKindPlugin(@NonNull Kind kind, @NonNull EventPluginIF eventPlugin) {
    this.kind = kind;
    this.eventPlugin = eventPlugin;
  }

  @Override
  public void processIncomingEvent(GenericEventKindIF event) {
    eventPlugin.processIncomingEvent(event);
  }
}
