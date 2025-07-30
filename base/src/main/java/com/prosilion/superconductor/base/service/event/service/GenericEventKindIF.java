package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.IEvent;
import com.prosilion.nostr.user.ISignableEntity;

public interface GenericEventKindIF extends EventIF, IEvent, ISignableEntity {
  String toBech32();
  String toString();
  boolean equals(Object o);
  int hashCode();
}
