package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import java.util.List;

public interface EventKindServiceIF<T extends Kind> {
  void processIncomingEvent(GenericEventKindIF event);
  T[] getKindArray();
  List<T> getKinds();
}
