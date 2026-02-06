package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.function.BiFunction;

public interface EventKindTypePluginIF extends EventPluginIF {
  Kind getKind();
  KindTypeIF getKindType();

  BiFunction<EventMaterializer, EventIF, ? extends BaseEvent>
      eventMaterializerFxn = EventMaterializer::materialize;

  BaseEvent materialize(EventIF eventIF);
}
