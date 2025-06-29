package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// DECORATOR BASE
public class EventKindTypePlugin implements EventKindTypePluginIF<KindTypeIF> {
  private final KindTypeIF kindType;

  private final EventKindPluginIF<Kind> eventKindPlugin;
  public EventKindTypePlugin(
      @NonNull KindTypeIF kindType,
      @NonNull EventKindPluginIF<Kind> eventKindPlugin) {
    this.kindType = kindType;
    this.eventKindPlugin = eventKindPlugin;
  }

  @Override
  public Kind getKind() {
    return eventKindPlugin.getKind();
  }

  @Override
  public KindTypeIF getKindType() {
    return kindType;
  }

  @Override
  public void processIncomingEvent(GenericEventKindTypeIF event) {
    eventKindPlugin.processIncomingEvent(event);
  }

  @Override
  public void processIncomingEvent(GenericEventKindIF event) {
    eventKindPlugin.processIncomingEvent(event);
  }
}
