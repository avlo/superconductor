package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;
import org.springframework.lang.NonNull;

public interface DeleteEventPluginIF extends EventPluginIF {
  void processIncomingEvent(@NonNull GenericEventKindIF event);
}
