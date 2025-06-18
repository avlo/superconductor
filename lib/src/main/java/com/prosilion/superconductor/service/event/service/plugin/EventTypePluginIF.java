package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.Type;
import com.prosilion.nostr.event.AbstractBadgeAwardEventIF;

public interface EventTypePluginIF<S extends Type, T extends AbstractBadgeAwardEventIF<S>, U extends Kind> {
  void processIncomingEvent(T event);

  U getKind();
}
