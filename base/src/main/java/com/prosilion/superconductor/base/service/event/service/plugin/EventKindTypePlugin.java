package com.prosilion.superconductor.base.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our BasicCar
public class EventKindTypePlugin implements EventKindTypePluginIF<KindTypeIF> {
  private final KindTypeIF kindType;
  //  TODO: review below, potential candidate for replacement with EventKindPlugin/IF
//    considerations: only current avail candidate bean is TextNote/Canonical
//                    but maybe a BadgeAwardEvent/etc base class fills that role
  private final EventPluginIF eventPlugin;

  public EventKindTypePlugin(@NonNull KindTypeIF kindType, @NonNull EventPluginIF eventPlugin) {
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
  public void processIncomingEvent(EventIF event) {
    eventPlugin.processIncomingEvent(event);
  }
}
