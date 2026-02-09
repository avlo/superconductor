package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our BasicCar
public class NonMaterializedEventKindPlugin implements EventKindPluginIF {
  @Getter
  private final Kind kind;  
  private final EventPluginIF eventPlugin;

  public NonMaterializedEventKindPlugin(
      @NonNull Kind kind,
      @NonNull EventPluginIF eventPlugin) {
    this.kind = kind;
    this.eventPlugin = eventPlugin;
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    eventPlugin.processIncomingEvent(event);
  }

  @Override
  public BaseEvent materialize(EventIF eventIF) {
    return new CanonicalEvent(eventIF.asGenericEventRecord());
  }

  private static class CanonicalEvent extends BaseEvent {
    public CanonicalEvent(@NonNull GenericEventRecord genericEventRecord) {
      super(genericEventRecord);
    }
  }
}
