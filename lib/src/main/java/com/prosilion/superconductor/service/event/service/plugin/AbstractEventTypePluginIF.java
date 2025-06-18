package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.Type;
import com.prosilion.nostr.event.AbstractBadgeAwardEventIF;

public interface AbstractEventTypePluginIF<T extends Kind, U extends Type, V extends AbstractBadgeAwardEventIF<U>> extends EventTypePluginIF<U, V, T> {
  U getType();
}
