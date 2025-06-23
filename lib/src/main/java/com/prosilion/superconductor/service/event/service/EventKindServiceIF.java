package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import java.util.List;

public interface EventKindServiceIF {
  void processIncomingEvent(GenericEventKindIF event);
  Kind[] getKindArray();
  List<Kind> getKinds();
}
