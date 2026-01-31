package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import java.util.List;

public interface EventKindServiceIF {
  void processIncomingEvent(EventIF event);
  List<Kind> getKinds();
}
