package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import java.util.Map;

public interface EventEntityOrDocumentServiceIF<T> {
  Map<Kind, Map<T, EventIF>> getAll();
}
