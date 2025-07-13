package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import java.util.Map;

public interface EventEntityOrDocumentServiceIF<T> {
  Map<Kind, Map<T, GenericEventKindIF>> getAll();
}
