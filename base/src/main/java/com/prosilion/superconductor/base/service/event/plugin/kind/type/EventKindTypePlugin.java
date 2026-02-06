package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our BasicCar
public class EventKindTypePlugin implements EventKindTypePluginIF {
  private final KindTypeIF kindType;
  //  TODO: review below, potential candidate for replacement with EventKindPlugin/IF
//    considerations: only current avail candidate bean is TextNote/Canonical
//                    but maybe a BadgeAwardEvent/etc base class fills that role
  private final EventPluginIF eventPlugin;
  private final EventMaterializer eventMaterializer;

  public EventKindTypePlugin(@NonNull KindTypeIF kindType, @NonNull EventPluginIF eventPlugin, @NonNull EventMaterializer eventMaterializer) {
    this.kindType = kindType;
    this.eventPlugin = eventPlugin;
    this.eventMaterializer = eventMaterializer;
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
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    eventPlugin.processIncomingEvent(event);
  }

  @Override
  public BaseEvent materialize(EventIF eventIF) {
    return eventMaterializer.materialize(eventIF);
  }
}
