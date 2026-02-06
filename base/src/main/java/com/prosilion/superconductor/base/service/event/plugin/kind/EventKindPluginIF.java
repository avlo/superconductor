package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import java.util.function.BiFunction;

public interface EventKindPluginIF extends EventPluginIF {
  Kind getKind();

  BiFunction<EventMaterializer, EventIF, ? extends BaseEvent>
      eventMaterializerFxn = EventMaterializer::materialize;

  BaseEvent materialize(EventIF eventIF);
}
