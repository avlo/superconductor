package com.prosilion.superconductor.base.service.event.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import java.util.List;

public interface EventKindServiceIF {
  <T extends BaseEvent> void processIncomingEvent(EventIF event);
  List<Kind> getKinds();
}
