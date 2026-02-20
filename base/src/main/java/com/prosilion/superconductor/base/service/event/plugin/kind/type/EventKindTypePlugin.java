package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our BasicCar
public class EventKindTypePlugin implements EventKindTypePluginIF {
  private final KindTypeIF kindType;
  //  TODO: review below, potential candidate for replacement with EventKindPlugin/IF
//    considerations: only current avail candidate bean is TextNote/Canonical
//                    but maybe a BadgeAwardEvent/etc base class fills that role
  private final EventPlugin eventPlugin;
  private final Function<EventIF, BaseEvent> eventMaterializer;

  public EventKindTypePlugin(
      @NonNull KindTypeIF kindType,
      @NonNull EventPlugin eventPlugin,
      @NonNull Function<EventIF, BaseEvent> eventMaterializer) {
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
  public GenericEventRecord processIncomingEvent(@NonNull EventIF event) {
    return eventPlugin.processIncomingEvent(
        eventMaterializer.apply(event));
  }
}
