package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import java.util.List;

public interface EventKindTypeServiceIF {
  void processIncomingEvent(GenericEventKindTypeIF event);
  void processIncomingEvent(GenericEventKindIF event);
  List<KindTypeIF> getKindTypes();
  List<Kind> getKinds();
}
