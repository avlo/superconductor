package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our BasicCar
public class EventKindTypePlugin<T extends BaseEvent> implements EventKindTypePluginIF<T> {
  private final KindTypeIF kindType;
  //  TODO: review below, potential candidate for replacement with EventKindPlugin/IF
//    considerations: only current avail candidate bean is TextNote/Canonical
//                    but maybe a BadgeAwardEvent/etc base class fills that role
  private final EventPluginIF<T> eventPlugin;

  public EventKindTypePlugin(@NonNull KindTypeIF kindType, @NonNull EventPluginIF<T> eventPlugin) {
    this.kindType = kindType;
    this.eventPlugin = eventPlugin;
  }

  @Override
  public Kind getKind() {
    return kindType.getKind();
  }

  @Override
  public KindTypeIF getKindType() {
    return kindType;
  }

  @Override
  public void processIncomingEvent(T event) {
    eventPlugin.processIncomingEvent(event);
  }
}
