package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import java.util.List;

public interface EventKindTypeServiceIF extends EventKindServiceIF {
  void processIncomingEvent(GenericEventKindTypeIF event);
  List<KindTypeIF> getKindTypes();
}
