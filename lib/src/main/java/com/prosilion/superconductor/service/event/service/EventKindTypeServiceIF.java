package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import java.util.List;

public interface EventKindTypeServiceIF<T extends Kind, U extends KindTypeIF> extends EventKindServiceIF<T> {
  void processIncomingKindTypeEvent(GenericEventKindTypeIF event);
  U[] getKindTypesArray();
  List<U> getKindTypes();
}
