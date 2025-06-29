package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.event.GenericEventKindIF;
import java.util.List;

public interface EventKindServiceIF<Kind> {
  void processIncomingEvent(GenericEventKindIF event);
  List<Kind> getKinds();
}
