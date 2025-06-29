package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import java.util.List;

public interface EventKindTypeServiceIF<KindTypeIF> extends EventKindServiceIF<Kind> {
  void processIncomingEvent(GenericEventKindTypeIF event);
  List<KindTypeIF> getKindTypes();
}
