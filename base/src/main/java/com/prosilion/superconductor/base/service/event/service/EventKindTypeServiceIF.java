package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import java.util.List;

public interface EventKindTypeServiceIF {
  void processIncomingEvent(EventIF event);
  void processIncomingEvent(GenericEventKindTypeIF event);
  List<KindTypeIF> getKindTypes();
  List<Kind> getKinds();
}
